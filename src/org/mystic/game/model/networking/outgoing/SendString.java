package org.mystic.game.model.networking.outgoing;

import org.mystic.game.model.networking.Client;
import org.mystic.game.model.networking.StreamBuffer;
import org.mystic.game.model.networking.out.OutgoingPacket;

public class SendString extends OutgoingPacket {

	private final String message;

	private final int id;

	public SendString(String message, int id) {
		super();
		this.message = message;
		this.id = id;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(message.length() + 6);
		out.writeVariableShortPacketHeader(client.getEncryptor(), 126);
		out.writeString(message);
		out.writeShort(id, StreamBuffer.ValueType.A);
		out.finishVariableShortPacketHeader();
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 126;
	}

}
