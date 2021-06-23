package com.miaxis.btlockcommander.util.packet;

public class LockVersion {
	public static int parseBody(byte[] buf, StringBuilder lockHardVersion, 
			StringBuilder lockSoftVersion, StringBuilder fingerHardVersion,
			StringBuilder fingerSoftVersion, StringBuilder nbSn, StringBuilder nbImei){
		int pos = 0;
		lockHardVersion.append(new String(buf, 0, 10).trim());
		pos += 10;
		
		lockSoftVersion.append(new String(buf, pos, 20).trim());
		pos += 20;
		
		fingerHardVersion.append(new String(buf, pos, 10).trim());
		pos += 10;

		fingerSoftVersion.append(new String(buf, pos, 20).trim());
		pos += 20;

		nbSn.append(new String(buf, pos, 15).trim());
		pos += 15;

		nbImei.append(new String(buf, pos, 17).trim());
		pos += 17;

		return 0;
	}
}
