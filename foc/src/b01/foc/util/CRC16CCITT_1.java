package b01.foc.util;

import b01.foc.Globals;

public class CRC16CCITT_1 {

	private   char text[] = null;
	private   char text_length;
	protected char crc;
	protected final static int poly = 0x1021; /* crc-ccitt mask */

	public CRC16CCITT_1(String sText) {
		this.text = sText.toCharArray();
		/*
		 * for(int i =0; i<text.length; i++){ String str =
		 * Integer.toHexString(text[i]); str = str.toUpperCase(); if(str.length() ==
		 * 1) str = "0"+str; System.out.print(str+" "); if((i+1) % 22 == 0)
		 * System.out.println(""); }
		 */
		go();
	}

	public void dispose(){
		text = null;
	}
	
	public char getCRC(){
		return crc;
	}
	
	private void go() {
		crc = 0xffff;
		text_length = 0;
		for (int i = 0; i < text.length; i++) {
			char ch = text[i];
			updateCrc(ch);
			text_length++;
		}
		augmentMessageForGoodCRC();

		crc ^= 0xffff;
		Globals.logString(Integer.toHexString(crc));
	}

	protected void updateCrc(char ch) {
		char i, v, xor_flag;

		/*
		 * Align test bit with leftmost bit of the message byte.
		 */
		v = 0x80;

		for (i = 0; i < 8; i++) {
			if ((crc & 0x8000) != 0) {
				xor_flag = 1;
			} else {
				xor_flag = 0;
			}
			crc = (char) (crc << 1);

			if ((ch & v) != 0) {
				/*
				 * Append next bit of message to end of CRC if it is not zero. The zero
				 * bit placed there by the shift above need not be changed if the next
				 * bit of the message is zero.
				 */
				crc = (char) (crc + 1);
			}

			if (xor_flag != 0) {
				crc = (char) (crc ^ poly);
			}

			/*
			 * Align test bit with next bit of the message byte.
			 */
			v = (char) (v >> 1);
		}
	}

	protected void augmentMessageForGoodCRC() {
		char i, xor_flag;

		for (i = 0; i < 16; i++) {
			if ((crc & 0x8000) != 0) {
				xor_flag = 1;
			} else {
				xor_flag = 0;
			}
			crc = (char) (crc << 1);

			if (xor_flag != 0) {
				crc = (char) (crc ^ poly);
			}
		}
	}
}
