package org.mystic.game.model.networking.outgoing;

import org.mystic.game.model.networking.Client;
import org.mystic.game.model.networking.StreamBuffer;
import org.mystic.game.model.networking.out.OutgoingPacket;

public class SendInterfaceConfig extends OutgoingPacket {

	private final int main;
	private final int sub1;
	private final int sub2;

	public SendInterfaceConfig(int main, int sub1, int sub2) {
		super();
		this.main = main;
		this.sub1 = sub1;
		this.sub2 = sub2;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(8);
		out.writeHeader(client.getEncryptor(), 246);
		out.writeShort(main, StreamBuffer.ByteOrder.LITTLE);
		out.writeShort(sub1);
		out.writeShort(sub2);
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 246;
	}

}
