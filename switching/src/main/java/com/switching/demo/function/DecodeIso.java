package com.switching.demo.function;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import java.io.InputStream;

public class DecodeIso {

    public ISOMsg parseISOMessage(String message){
        ISOMsg isoMsg = new ISOMsg();
        try {
            // Load package from resources directory.
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            isoMsg.setPackager(packager);
            isoMsg.unpack(message.getBytes());

        } catch (Exception e) {
            System.out.println (e.getMessage ());
        }
        return isoMsg;
    }
    public void printISOMessage(ISOMsg isoMsg) {
        try {
            System.out.printf("MTI = %s%n", isoMsg.getMTI());
            for (int i = 1; i <= isoMsg.getMaxField(); i++) {
                if (isoMsg.hasField(i)) {
                    System.out.printf("Field (%s) = %s%n", i, isoMsg.getString(i));
                }
            }
        } catch (ISOException e) {
            e.printStackTrace();
        }
    }
}
