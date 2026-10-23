package com.hippo.ducttapecore.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageResetDeathState implements IMessage {

    private float health;

    public MessageResetDeathState() {
    }

    public MessageResetDeathState(float health) {
        this.health = health;
    }

    public float getHealth() {
        return health;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        health = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(health);
    }
}
