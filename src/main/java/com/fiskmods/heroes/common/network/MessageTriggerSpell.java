package com.fiskmods.heroes.common.network;

import java.util.ArrayList;

import com.fiskmods.heroes.common.entity.EntitySpellDuplicate;
import com.fiskmods.heroes.common.spell.Spell;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageTriggerSpell extends AbstractMessage<MessageTriggerSpell>
{
    private Spell spell;
    private int id;

    public MessageTriggerSpell()
    {
    }

    public MessageTriggerSpell(EntityPlayer player, Spell spellObj)
    {
        id = player.getEntityId();
        spell = spellObj;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        spell = Spell.getSpellFromName(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        ByteBufUtils.writeUTF8String(buf, spell.getName());
    }

    @Override
    public void receive() throws MessageException
    {
        EntityPlayer caster = getSender(id);

        if (spell != null && (context.side.isClient() || spell.spellCooldown.available(caster)) && spell.canTrigger(caster))
        {
            if (context.side.isServer() || spell.shouldSync(caster))
            {
                spell.onTrigger(caster);

                if (spell.canDuplicatesUse)
                {
                    for (Object obj : new ArrayList<>(caster.worldObj.loadedEntityList))
                    {
                        if (obj instanceof EntitySpellDuplicate)
                        {
                            EntitySpellDuplicate entity = (EntitySpellDuplicate) obj;

                            if (entity.isOwner(caster) && spell.canTrigger(entity))
                            {
                                spell.onTrigger(entity);
                            }
                        }
                    }
                }
            }

            spell.spellCooldown.set(caster);

            if (context.side.isServer())
            {
                spell.trigger(caster);
            }
        }
    }
}
