package org.mystic.game.model.networking.outgoing;

import org.mystic.game.model.networking.Client;
import org.mystic.game.model.networking.StreamBuffer;
import org.mystic.game.model.networking.out.OutgoingPacket;

/**
 * Moves a component on an interface
 * 
 * @author Michael Sasse
 * 
 */
public class SendMoveComponent extends OutgoingPacket {

	private final int x, y, componentId;

	public SendMoveComponent(int x, int y, int componentId) {
		this.x = x;
		this.y = y;
		this.componentId = componentId;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(7);
		out.writeHeader(client.getEncryptor(), 70);
		out.writeShort(x);
		out.writeShort(y, StreamBuffer.ByteOrder.LITTLE);
		out.writeShort(componentId, StreamBuffer.ByteOrder.LITTLE);
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 70;
	}

}
