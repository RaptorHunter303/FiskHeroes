package com.fiskmods.heroes.common.network;

import com.fiskmods.heroes.common.gameboii.GameboiiSaveType;
import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.GameboiiCartridge;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageGameboii
{
    public static class RequestStats extends AbstractMessage<RequestStats>
    {
        private GameboiiCartridge cartridge;
        private GameboiiSaveType saveType;

        public RequestStats()
        {
        }

        public RequestStats(GameboiiCartridge cartridge, GameboiiSaveType saveType)
        {
            this.cartridge = cartridge;
            this.saveType = saveType;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            cartridge = GameboiiCartridge.values()[buf.readByte() % GameboiiCartridge.values().length];
            saveType = GameboiiSaveType.values()[buf.readByte() % GameboiiSaveType.values().length];
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(cartridge.ordinal());
            buf.writeByte(saveType.ordinal());
        }

        @Override
        public void receive() throws MessageException
        {
            EntityPlayer player = context.getServerHandler().playerEntity;

            try
            {
                byte[] data = saveType.load(player).loadData(cartridge);

                if (data != null)
                {
                    SHNetworkManager.wrapper.sendTo(new Load(cartridge, data), (EntityPlayerMP) player);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private static abstract class SaveLoad<T extends AbstractMessage<T>> extends AbstractMessage<T>
    {
        GameboiiCartridge cartridge;
        byte[] data;

        public SaveLoad()
        {
        }

        public SaveLoad(GameboiiCartridge cartridge, byte[] data)
        {
            this.cartridge = cartridge;
            this.data = data;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            cartridge = GameboiiCartridge.values()[buf.readByte() % GameboiiCartridge.values().length];
            data = new byte[buf.readInt()];
            buf.readBytes(data);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(cartridge.ordinal());
            buf.writeInt(data.length);
            buf.writeBytes(data);
        }
    }

    public static class Load extends SaveLoad<Load>
    {
        public Load()
        {
        }

        public Load(GameboiiCartridge cartridge, byte[] data)
        {
            super(cartridge, data);
        }

        @Override
        public void receive() throws MessageException
        {
            try
            {
                Gameboii.get(cartridge).readSaveData(data);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static class Save extends SaveLoad<Save>
    {
        private GameboiiSaveType saveType;

        public Save()
        {
        }

        public Save(GameboiiSaveType saveType, GameboiiCartridge cartridge, byte[] data)
        {
            super(cartridge, data);
            this.saveType = saveType;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            saveType = GameboiiSaveType.values()[buf.readByte() % GameboiiSaveType.values().length];
            super.fromBytes(buf);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(saveType.ordinal());
            super.toBytes(buf);
        }

        @Override
        public void receive() throws MessageException
        {
            EntityPlayer player = context.getServerHandler().playerEntity;

            try
            {
                saveType.load(player).saveData(data, cartridge);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
