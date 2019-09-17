package com.bank.atm.client.function;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PurchaseIso {
    private ISOMsg isoMsg;
    private InputStream is;
    private GenericPackager packager;
    private byte[] result = new byte[0];

    public String buildIsoMessageInquiry(String rekening, String pin, String noTelp, int jumlahPulsa, String server, int port){
        try {
            // Load package from resources directory.
            is = getClass().getResourceAsStream("/fields.xml");
            packager = new GenericPackager (is);

            isoMsg = new ISOMsg ();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0200");
            isoMsg.set (1,"0000000000000000");
            isoMsg.set (2,rekening);
            isoMsg.set(3, "370000");
            isoMsg.set(4, jumlahPulsa+"");
            isoMsg.set(7, new SimpleDateFormat ("MMddHHmmss").format(new Date ()));
            isoMsg.set(11, "000000");
            isoMsg.set (12, new SimpleDateFormat ("hhmmss").format(new Date ()));
            isoMsg.set (13, new SimpleDateFormat ("MMdd").format(new Date ()));
            isoMsg.set (15, new SimpleDateFormat ("MMdd").format(new Date ()));
            isoMsg.set(18,"0000");
            isoMsg.set(32,"00000000000");
            isoMsg.set(33,"1131");
            isoMsg.set (37,"000000000000");
            isoMsg.set (41, "00000000");
            isoMsg.set (42,"000000000000000");
            isoMsg.set(43,"0000000000000000000000000000000000000000");
            isoMsg.set (49,"000");
            isoMsg.set (48,server+port);
            isoMsg.set (62, noTelp);
            isoMsg.set (102,"0");
            isoMsg.set (52, pin);
            result= isoMsg.pack();
        } catch (ISOException e) {
            System.out.println ("Error: "+e.getMessage ());
        }
        return new String(result);
    }

    public String buildIsoMessagePurchase(String rekening, String pin, String noTelp, int jumlahPulsa, String server, int port){
        try {
            // Load package from resources directory.
            is = getClass().getResourceAsStream("/fields.xml");
            packager = new GenericPackager(is);

            isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0200");
            isoMsg.set (1,"0000000000000000");
            isoMsg.set (2,rekening);
            isoMsg.set(3, "170000");
            isoMsg.set(4, jumlahPulsa+"");
            isoMsg.set(7, new SimpleDateFormat ("MMddHHmmss").format(new Date ()));
            isoMsg.set(11, "000000");
            isoMsg.set (12, new SimpleDateFormat ("hhmmss").format(new Date ()));
            isoMsg.set (13, new SimpleDateFormat ("MMdd").format(new Date ()));
            isoMsg.set (15, new SimpleDateFormat ("MMdd").format(new Date ()));
            isoMsg.set(18,"0000");
            isoMsg.set(32,"00000000000");
            isoMsg.set(33,"1131");
            isoMsg.set (37,"000000000000");
            isoMsg.set (41, "00000000");
            isoMsg.set (42,"000000000000000");
            isoMsg.set(43,"0000000000000000000000000000000000000000");
            isoMsg.set (48,server+port);
            isoMsg.set (49,"000");
            isoMsg.set (62, noTelp);
            isoMsg.set (102,"0");
            isoMsg.set (52, pin);
            result= isoMsg.pack();
        } catch (ISOException e) {
            System.out.println ("Error: "+e.getMessage ());
        }
        return new String(result);
    }

    public String responseIsoMessageInquiry(String statusCode, int balanced){
        try {
            // Load package from resources directory.
            is = getClass().getResourceAsStream("/fields.xml");
            packager = new GenericPackager(is);

            isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0210");
            isoMsg.set (1,"0000000000000000");
            isoMsg.set(3, "380000");
            isoMsg.set(4, "000000000000");
            isoMsg.set(7, new SimpleDateFormat ("MMddHHmmss").format(new Date ()));
            isoMsg.set(11, "000000");
            isoMsg.set (12, new SimpleDateFormat ("hhmmss").format(new Date ()));
            isoMsg.set (13, new SimpleDateFormat ("MMdd").format(new Date ()));
            isoMsg.set (15, new SimpleDateFormat ("MMdd").format(new Date ()));
            isoMsg.set(18,"0000");
            isoMsg.set(32,"00000000000");
            isoMsg.set(33,"1131");
            isoMsg.set (37,"000000000000");
            isoMsg.set (39, statusCode);
            isoMsg.set (41, "00000000");
            isoMsg.set (42,"000000000000000");
            isoMsg.set(43,"0000000000000000000000000000000000000000");
            isoMsg.set (49,"000");
            isoMsg.set (62, balanced+"");
            isoMsg.set (102,"0");
            DecodeIso decodeIso = new DecodeIso ();
            decodeIso.printISOMessage(isoMsg);
            result= isoMsg.pack();
            System.out.println ("ISO Message from Server: "+ new String(result));
        } catch (ISOException e) {
            System.out.println ("Error: "+e.getMessage ());
        }
        return new String(result);
    }

    public String buildResponseMessage(String rekening, String statusCode, int balanced){
        try {
            // Load package from resources directory.
            is = getClass().getResourceAsStream("/fields.xml");
            packager = new GenericPackager(is);

            isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0210");
            isoMsg.set (1,"0000000000000000");
            isoMsg.set (2,rekening);
            isoMsg.set(3, "180000");
            isoMsg.set(4, "000000000000");
            isoMsg.set(7, new SimpleDateFormat ("MMddHHmmss").format(new Date ()));
            isoMsg.set(11, "000000");
            isoMsg.set (12, new SimpleDateFormat ("hhmmss").format(new Date ()));
            isoMsg.set (13, new SimpleDateFormat ("MMdd").format(new Date ()));
            isoMsg.set (15, new SimpleDateFormat ("MMdd").format(new Date ()));
            isoMsg.set(18,"0000");
            isoMsg.set(32,"00000000000");
            isoMsg.set(33,"1131");
            isoMsg.set (37,"000000000000");
            isoMsg.set (39, statusCode);
            isoMsg.set (41, "00000000");
            isoMsg.set (42,"000000000000000");
            isoMsg.set(43,"0000000000000000000000000000000000000000");
            isoMsg.set (49,"000");
            isoMsg.set (54, balanced+"");
            isoMsg.set (62, "0");
            isoMsg.set (102,"0");

            DecodeIso decodeIso = new DecodeIso ();
            decodeIso.printISOMessage(isoMsg);
            result= isoMsg.pack();
            System.out.println ("ISO Message from Server: "+ new String(result));
        } catch (ISOException e) {
            System.out.println ("Error: "+e.getMessage ());
        }
        return new String(result);
    }
}
