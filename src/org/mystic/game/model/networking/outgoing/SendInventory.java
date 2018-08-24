package org.mystic.game.model.networking.outgoing;

import org.mystic.game.model.entity.item.Item;
import org.mystic.game.model.networking.Client;
import org.mystic.game.model.networking.StreamBuffer;
import org.mystic.game.model.networking.out.OutgoingPacket;

public class SendInventory extends OutgoingPacket {

	private final Item[] items;

	public SendInventory(Item[] items) {
		super();
		this.items = items;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(256);
		out.writeVariableShortPacketHeader(client.getEncryptor(), 53);
		out.writeShort(3214);
		out.writeShort(28);
		for (int i = 0; i < 28; i++) {
			if (items[i] == null) {
				out.writeByte(0);
				out.writeShort(0, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
			} else {
				if (items[i].getAmount() > 254) {
					out.writeByte(255);
					out.writeInt(items[i].getAmount(), StreamBuffer.ByteOrder.INVERSE_MIDDLE);
				} else {
					out.writeByte(items[i].getAmount());
				}
				out.writeShort(items[i].getId() + 1, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
			}
		}
		out.finishVariableShortPacketHeader();
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 53;
	}

}