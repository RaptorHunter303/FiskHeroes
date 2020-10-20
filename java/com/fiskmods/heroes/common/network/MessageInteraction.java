package com.fiskmods.heroes.common.network;

import com.fiskmods.heroes.common.interaction.Interaction;
import com.fiskmods.heroes.common.interaction.InteractionType;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageInteraction extends AbstractMessage<MessageInteraction>
{
    private int id, x, y, z;
    private Interaction interaction;
    private InteractionType type;

    public MessageInteraction()
    {
    }

    public MessageInteraction(EntityPlayer player, Interaction interaction, InteractionType type, int x, int y, int z)
    {
        id = player.getEntityId();
        this.interaction = interaction;
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        interaction = Interaction.getInteraction(ByteBufUtils.readUTF8String(buf));
        type = InteractionType.values()[Math.abs(buf.readByte()) % InteractionType.values().length];
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        ByteBufUtils.writeUTF8String(buf, interaction.getRegistryKey());
        buf.writeByte(type.ordinal());
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public void receive() throws MessageException
    {
        if (interaction != null)
        {
            EntityPlayer sender = getSender(id);
            EntityPlayer clientPlayer = getPlayer();

            if (context.side.isClient())
            {
                if (sender != clientPlayer)
                {
                    interaction.tryListen(sender, clientPlayer, type, Side.CLIENT, x, y, z);
                }
            }
            else if (interaction.tryListen(sender, clientPlayer, type, Side.SERVER, x, y, z))
            {
                TargetPoint target = interaction.getTargetPoint(sender, x, y, z);

                if (target == null)
                {
                    SHNetworkManager.wrapper.sendToDimension(this, sender.dimension);
                }
                else if (target.range > 0)
                {
                    SHNetworkManager.wrapper.sendToAllAround(this, target);
                }
            }
        }
    }
}
