package com.fiskmods.heroes.common.network;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.DimensionalCoords;
import com.fiskmods.heroes.util.NBTHelper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class MessageTileTrigger extends AbstractMessage<MessageTileTrigger>
{
    private DimensionalCoords coords;
    private int id;
    private byte[] data;

    public MessageTileTrigger()
    {
    }

    private MessageTileTrigger(DimensionalCoords coords, @Nonnull EntityPlayer player, byte[] data)
    {
        this.coords = coords;
        this.data = data;
        id = player.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        coords = NBTHelper.fromBytes(buf, DimensionalCoords.class);

        data = new byte[buf.readableBytes()];
        buf.readBytes(data);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        NBTHelper.toBytes(buf, coords);

        buf.writeBytes(data);
    }

    @Override
    public void receive() throws MessageException
    {
        EntityPlayer sender = getSender(id);

        if (context.side.isClient() && sender == FiskHeroes.proxy.getPlayer())
        {
            return;
        }

        TileEntity tile = getWorld(coords.dimension).getTileEntity(coords.posX, coords.posY, coords.posZ);

        if (tile instanceof ITileDataCallback)
        {
            ((ITileDataCallback) tile).receive(sender, Unpooled.copiedBuffer(data));

            if (context.side.isServer())
            {
                SHNetworkManager.wrapper.sendToDimension(new MessageTileTrigger(coords, sender, data), coords.dimension);
            }
        }
    }

    public interface ITileDataCallback
    {
        /**
         * Called when a tile gets triggered
         *
         * @param sender The player who triggered it
         * @param buf A buffer containing the byte data
         */
        void receive(EntityPlayer sender, ByteBuf buf);

        default void sendToServer(Consumer<ByteBuf> c)
        {
            TileEntity tile = (TileEntity) this;

            if (tile.getWorldObj().isRemote)
            {
                EntityPlayer from = FiskHeroes.proxy.getPlayer();
                ByteBuf buf = Unpooled.buffer();
                c.accept(buf);

                SHNetworkManager.wrapper.sendToServer(new MessageTileTrigger(new DimensionalCoords(tile), from, buf.array()));
                receive(from, buf);
            }
        }
    }
}
