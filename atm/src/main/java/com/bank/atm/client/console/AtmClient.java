package com.bank.atm.client.console;

import com.bank.atm.client.function.*;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class AtmClient {
    private static String accountNumber;
    private static String pinNumber;
    private static Scanner scanner = new Scanner (System.in);
    private static final Logger logger = LoggerFactory.getLogger(AtmClient.class);
    private static String server;
    private static int port;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static DataInputStream dis;

    public   AtmClient( String server, int port) {
        this.server = server;
        this.port = port;
    }

    public static void main(String[] args) {
        ClientHttp clientHttp = new ClientHttp ();
        DecodeIso decodeIso = new DecodeIso ();
        System.out.print ("Masukkan Rekening: ");
        accountNumber = scanner.next ();
        System.out.print ("Masukkan Pin: ");
        pinNumber = scanner.next ();
        int ans =0;
        String message="";
        String url="";
        String respon;
        ISOMsg isoMsg;
        logger.info ("Application is running");
        logger.info ("Account Number : {}",accountNumber);
        logger.info ("Pin Number : {}",pinNumber);
        do{
            showMenu ();
            ans = scanner.nextInt ();

            stop:
            switch (ans){
                case 1:
                    logger.info ("Doing Cashwithdrawal");
                    int nominal = nominalTunai ();
                    if(nominal == 0){
                        break stop;
                    }
                    logger.info ("Nominal : {}",nominal);
                    CashWithdrawalIso cashWithdrawalIso = new CashWithdrawalIso ();
                    message = cashWithdrawalIso.buildIsoMessage (accountNumber,pinNumber, nominal, server,port);
                    logger.info ("ISO Message : {}",message);
                    try{
                        clientHttp.sendHttp (message,"send");
                        serverSocket = new ServerSocket(port);
                        clientSocket=serverSocket.accept();//establishes connection
                        dis = new DataInputStream(clientSocket.getInputStream());
                        respon = (String)dis.readUTF();

                        serverSocket.close ();
                        clientSocket.close ();
                        dis.close ();

                        logger.info ("Get respon from server : {}",respon);
                        isoMsg = decodeIso.parseISOMessage (respon);
                        String statusCode = isoMsg.getString (39);
                        if("00".equalsIgnoreCase (statusCode)) {
                            System.out.println ("\t\t No Rekening: " + accountNumber);
                            System.out.println ("\t\t Uang yang ditarik: Rp." + nominal);
                            System.out.println ("\t\t Sisa saldo: Rp." + isoMsg.getString (54));
                            logger.info ("Total amount in bank : {}",isoMsg.getString (54));
                            break stop;
                        }else if("51".equalsIgnoreCase (statusCode)){
                            System.out.println ();
                            System.out.println ("\t\t\t Maaf, Saldo anda tidak mencukupi.");
                            logger.warn ("Insufficient balance remaining");
                            logger.info ("Amount : {}",isoMsg.getString (54));
                            System.out.println ("\t\t No Rekening: "+accountNumber);
                            System.out.println ("\t\t Sisa saldo: Rp."+isoMsg.getString (54));
                            break stop;
                        }else if("05".equalsIgnoreCase (statusCode)){
                            System.out.println ("AUTENTIKASI GAGAL :(");
                            logger.warn ("Failed authentication");
                            break stop;
                        }
                    }catch (Exception e){
                        logger.error ("Error : {} in method {}",e.getMessage (), e.getStackTrace ());
                    }

                    break;
                case 2:
                    logger.info ("Doing transfer");
                    TransferIso transferIso = new TransferIso ();
                    TransferToOtherIso transferToOtherIso = new TransferToOtherIso ();
                    System.out.println (">> 1. Transfer Sesama");
                    System.out.println (">> 2. Transfer Bank Lain");
                    System.out.print("=> ");
                    String pilih = scanner.next ();
                    int amounts = 0;
                    String forwardNumber="";
                    String bankCode="";
                    String destinationAccountNumber = null;
                    if(pilih.equalsIgnoreCase ("1")){
                        logger.info ("Transfer to in bank");
                        url = "transferInquiry";
                        System.out.println ("\t\t Masukkan Nomor Rekening Tujuan \t\t");
                        System.out.print ("=> ");
                        destinationAccountNumber = scanner.next ();
                        logger.info ("Transfer Inquiry");
                        logger.info ("Account number forwarding to: {}",destinationAccountNumber);
                        System.out.println ("\t\t Jumlah Transfer \t\t");
                        System.out.print ("=> ");
                        amounts = scanner.nextInt ();
                        logger.info ("Transfer amount: {}",amounts);
                        message = transferIso.buildIsoMessageInquiry (accountNumber, pinNumber, destinationAccountNumber, amounts, server, port);
                        logger.info ("Iso Message : {}",message);
//                        respon = clientHttp.sendHttp (message, url);
                        try{
                            clientHttp.sendHttp (message,"send");
                            serverSocket = new ServerSocket(port);
                            clientSocket=serverSocket.accept();//establishes connection
                            dis = new DataInputStream(clientSocket.getInputStream());
                            respon = (String)dis.readUTF();
                            serverSocket.close ();
                            clientSocket.close ();
                            dis.close ();
                            logger.info ("Get response Iso Message : {}",respon);

                            isoMsg = decodeIso.parseISOMessage (respon);
                            String statusCode = isoMsg.getString (39);
                            String atasNama = isoMsg.getString (103);
                            String saldo = isoMsg.getString (62);
                            if("00".equalsIgnoreCase (statusCode)){
                                System.out.println ("\t\t Atas Nama\t\t\t : "+atasNama);
                                logger.info ("Send balance to: {}",atasNama);
                                System.out.println ("\t\t No Rekening Tujuan  : "+destinationAccountNumber);
                                System.out.println ("\t\t Nominal\t\t\t : Rp. "+amounts);
                                System.out.println ();
                                System.out.println ("Apakah anda yakin?  \t >> YA \n\t\t\t\t\t\t >> TIDAK");
                                System.out.print ("=> ");
                                String jawaban = scanner.next ();
                                if(jawaban.equalsIgnoreCase ("ya")){
                                    message = transferIso.buildIsoMessageTransfer (accountNumber, destinationAccountNumber,amounts, server, port);
                                    clientHttp.sendHttp (message,"send");
                                    serverSocket = new ServerSocket(port);
                                    clientSocket=serverSocket.accept();//establishes connection
                                    dis = new DataInputStream(clientSocket.getInputStream());
                                    respon = (String)dis.readUTF();
                                    isoMsg = decodeIso.parseISOMessage (respon);
                                    System.out.println ("\t\t Transfer Berhasil");
                                    logger.info ("Transfer Successfully");
                                    serverSocket.close ();
                                    clientSocket.close ();
                                    dis.close ();
                                }
                                break stop;
                            }else if("76".equalsIgnoreCase (statusCode)){
                                System.out.println ("\t\t Nomor Rekening Tujuan Salah");
                                logger.info ("Forwarding Account Number is wrong");
                                serverSocket.close ();
                                clientSocket.close ();
                                dis.close ();
                                break stop;
                            }else if("51".equalsIgnoreCase (statusCode)){
                                System.out.println ();
                                logger.warn ("Insufficient balance remaining");
                                logger.info ("Amount : {}",isoMsg.getString (54));
                                System.out.println ("\t Maaf, Saldo anda tidak mencukupi.");
                                System.out.println ("\t\t No Rekening: "+accountNumber);
                                System.out.println ("\t\t Sisa saldo: Rp."+saldo);
                                serverSocket.close ();
                                clientSocket.close ();
                                dis.close ();
                                break stop;
                            }else if("05".equalsIgnoreCase (statusCode)) {
                                System.out.println ("AUTENTIKASI GAGAL :(");
                                logger.warn ("Failed authentication");
                                serverSocket.close ();
                                clientSocket.close ();
                                dis.close ();
                                break stop;
                            }

                        }catch (Exception e){
                            logger.error ("Error : {} in method {}",e.getMessage (), e.getStackTrace ());
                        }
                    }else{
                        logger.info ("Transfer into another bank");
                        System.out.println ("\t\t Masukkan Nomor Rekening Tujuan Beserta Kode Bank (003)\t\t");
                        System.out.print ("=> ");
                        destinationAccountNumber = scanner.next ();
                        System.out.println ("\t\t Jumlah Transfer \t\t");
                        System.out.print ("=> ");
                        amounts = scanner.nextInt ();
                        logger.info ("Transfer amount : {}",amounts);
                        bankCode = destinationAccountNumber.substring (0,3);
                        forwardNumber = destinationAccountNumber.substring (3);
                        message = transferToOtherIso.buildIsoMessageInquiry (accountNumber, pinNumber, forwardNumber, bankCode,amounts, server, port);
                        logger.info ("Iso Message : {}",message);
//                        respon = clientHttp.sendHttp (message, url);
//                        logger.info ("Get response Iso Message: {}",respon);

                        try{
                            clientHttp.sendHttp (message,"send");
                            serverSocket = new ServerSocket(port);
                            clientSocket=serverSocket.accept();//establishes connection
                            dis = new DataInputStream(clientSocket.getInputStream());
                            respon = (String)dis.readUTF();

                            serverSocket.close ();
                            clientSocket.close ();
                            dis.close ();

                            logger.info ("Get response Iso Message : {}",respon);

                            isoMsg = decodeIso.parseISOMessage (respon);
                            String statusCode = isoMsg.getString (39);
                            String atasNama = isoMsg.getString (103);
                            String saldo = isoMsg.getString (62);
                            int transactionAmount = Integer.parseInt (isoMsg.getString (4));
                            if("00".equalsIgnoreCase (statusCode)){
                                System.out.println ("\t\t Atas Nama\t\t\t : "+atasNama);
                                logger.info ("Send balance to: {}",atasNama);
                                System.out.println ("\t\t No Rekening Tujuan  : "+destinationAccountNumber.substring (3));
                                System.out.println ("\t\t Nominal\t\t\t : Rp."+transactionAmount);
                                System.out.println ();
                                System.out.println ("Apakah anda yakin?  \t >> YA \n\t\t\t\t\t\t >> TIDAK");
                                System.out.print ("=> ");
                                String jawaban = scanner.next ();
                                if(jawaban.equalsIgnoreCase ("ya")){
                                    message = transferToOtherIso.buildIsoMessageTransfer (accountNumber, forwardNumber, bankCode,transactionAmount, server, port);

                                    clientHttp.sendHttp (message,"send");
                                    serverSocket = new ServerSocket(port);
                                    clientSocket=serverSocket.accept();//establishes connection
                                    dis = new DataInputStream(clientSocket.getInputStream());
                                    respon = (String)dis.readUTF();

                                    isoMsg = decodeIso.parseISOMessage (respon);
                                    System.out.println ("\t\t Transfer Berhasil");
                                    logger.info ("Transfer Successfully");
                                    serverSocket.close ();
                                    clientSocket.close ();
                                    dis.close ();
                                }
                                break stop;
                            }else if("76".equalsIgnoreCase (statusCode)){
                                System.out.println ("\t\t Nomor Rekening Tujuan Salah");
                                logger.info ("Forwarding Account Number is wrong");
                                serverSocket.close ();
                                clientSocket.close ();
                                dis.close ();
                                break stop;
                            }else if("51".equalsIgnoreCase (statusCode)){
                                System.out.println ();
                                logger.warn ("Insufficient balance remaining");
                                logger.info ("Amount : {}",isoMsg.getString (54));
                                System.out.println ("\t Maaf, Saldo anda tidak mencukupi.");
                                System.out.println ("\t\t No Rekening: "+accountNumber);
                                System.out.println ("\t\t Sisa saldo: Rp. "+saldo);
                                serverSocket.close ();
                                clientSocket.close ();
                                dis.close ();
                                break stop;
                            }else if("05".equalsIgnoreCase (statusCode)) {
                                System.out.println ("AUTENTIKASI GAGAL :(");
                                logger.warn ("Failed authentication");
                                serverSocket.close ();
                                clientSocket.close ();
                                dis.close ();
                                break stop;
                            }

                        }catch (Exception e){
                            logger.error ("Error : {} in method {}",e.getMessage (), e.getStackTrace ());
                        }
                    }
                    break;
                case 3:
                    logger.info ("Doing Purchasing");
                    String phoneNumber="";
                    PurchaseIso purchaseIso = new PurchaseIso ();
                    System.out.println ("\t\t Masukkan Nomor Telepon \t\t");
                    System.out.print ("=> ");
                    phoneNumber = scanner.next ();
                    logger.info ("Phone Number: {}",phoneNumber);
                    int pulsa = nominalPulsa ();
                    logger.info ("Nominal: {}",pulsa);
                    message = purchaseIso.buildIsoMessageInquiry (accountNumber,pinNumber,phoneNumber,pulsa, server,port);
                    try {
//                        respon = clientHttp.sendHttp (message, url);
                        clientHttp.sendHttp (message,"send");
                        serverSocket = new ServerSocket(port);
                        clientSocket=serverSocket.accept();//establishes connection
                        dis = new DataInputStream(clientSocket.getInputStream());
                        respon = (String)dis.readUTF();
                        logger.info ("Get response Iso Message : {}",respon);

                        isoMsg = decodeIso.parseISOMessage (respon);
                        String statusCode = isoMsg.getString (39);
                        int saldo = Integer.parseInt (isoMsg.getString (62));

                        serverSocket.close ();
                        clientSocket.close ();
                        dis.close ();
                        if("00".equalsIgnoreCase (statusCode)){
                            System.out.println ("\t\t Nomor Telepon\t\t\t : "+phoneNumber);
                            System.out.println ("\t\t Nominal Pulsa\t\t\t : Rp. "+pulsa);
                            System.out.println ();
                            System.out.println ("Apakah anda yakin?  \t >> YA \n\t\t\t\t\t\t >> TIDAK");
                            System.out.print ("=> ");
                            String jawaban = scanner.next ();
                            if(jawaban.equalsIgnoreCase ("ya")){
                                String newUrl = "purchase";
                                message = purchaseIso.buildIsoMessagePurchase (accountNumber, pinNumber, phoneNumber, pulsa, server, port);
                                clientHttp.sendHttp (message,"send");
                                serverSocket = new ServerSocket(port);
                                clientSocket=serverSocket.accept();//establishes connection
                                dis = new DataInputStream(clientSocket.getInputStream());
                                respon = (String)dis.readUTF();

                                respon = clientHttp.sendHttp (message, newUrl);
                                isoMsg = decodeIso.parseISOMessage (respon);
                                System.out.println ("\t\t Pembelian Berhasil");
                                logger.info ("Purchasing Successfully");
                                serverSocket.close ();
                                clientSocket.close ();
                                dis.close ();
                            }
                            break stop;
                        }else if("76".equalsIgnoreCase (statusCode)){
                            System.out.println ("\t\t Maaf, Nomor Telepon tidak terdaftar");
                            System.out.println ("\t\t Silahkan periksa kembali nomor tujuan anda");
                            logger.info ("Forwarding Account Number is wrong");
                            break stop;
                        }else if("51".equalsIgnoreCase (statusCode)){
                            System.out.println ();
                            logger.warn ("Insufficient balance remaining");
                            logger.info ("Amount : {}",isoMsg.getString (54));
                            System.out.println ("\t Maaf, Saldo anda tidak mencukupi.");
                            System.out.println ("\t\t No Rekening: "+accountNumber);
                            System.out.println ("\t\t Sisa saldo: Rp. "+saldo);
                            serverSocket.close ();
                            clientSocket.close ();
                            dis.close ();
                            break stop;
                        }else if("05".equalsIgnoreCase (statusCode)) {
                            System.out.println ("AUTENTIKASI GAGAL :(");
                            logger.warn ("Failed authentication");
                            serverSocket.close ();
                            clientSocket.close ();
                            dis.close ();
                            break stop;
                        }

                    }catch (Exception e){
                        logger.error ("Error : {} in method {}",e.getMessage (), e.getStackTrace ());
                    }
                    break;
                case 4:
                    logger.info ("Doing Check Balance");
                    BalanceIso balancedIso = new BalanceIso ();
                    message = balancedIso.buildIsoMessage (accountNumber, pinNumber, server, port);
                    try {
                        clientHttp.sendHttp (message,"send");
                        serverSocket = new ServerSocket(port);
                        clientSocket=serverSocket.accept();//establishes connection
                        logger.info ("Receive connection from {} on port {}", clientSocket.getInetAddress (), clientSocket.getPort ());
                        dis = new DataInputStream(clientSocket.getInputStream());
                        respon = (String)dis.readUTF();
                        logger.info ("Get response Iso Message : {}",respon);

                        isoMsg = decodeIso.parseISOMessage (respon);
                        String statusCode = isoMsg.getString (39);
                        if("00".equalsIgnoreCase (statusCode)){
                            System.out.println ("\t\tNo Rekening: "+accountNumber);
                            System.out.println ("\t\tSaldo: Rp. "+isoMsg.getString (54));
                            logger.info ("Insufficient balance: {}",isoMsg.getString (54));
                            serverSocket.close ();
                            clientSocket.close ();
                            dis.close ();
                            break stop;
                        }else {
                            System.out.println ("AUTENTIKASI GAGAL :(");
                            logger.warn ("Failed authentication");
                            serverSocket.close ();
                            clientSocket.close ();
                            dis.close ();
                            serverSocket.close ();
                            clientSocket.close ();
                            dis.close ();
                            break stop;
                        }
                    }
                    catch (Exception e) {
                        logger.error ("Error : {} in method {}",e.getMessage (), e.getStackTrace ());
                    }

                    break;
                case 5:
                    logger.info ("Payment");
                    PaymentIso paymentIso = new PaymentIso ();
                    System.out.println ("\t\tMasukkan Virtual Account DANA \t\t");
                    System.out.print ("=> ");
                    String virtualAccount = scanner.next ();
                    logger.info ("DANA Virtual Account: {}",virtualAccount);
                    System.out.println ("\t\tMasukkan Nilai Pembayaran \t\t");
                    System.out.print ("=> ");
                    int amount = scanner.nextInt ();
                    logger.info ("Nominal: {}",amount);
                    message = paymentIso.buildIsoMessageInquiry (accountNumber, pinNumber, virtualAccount, amount, server,port);
                    logger.info ("Iso Message: {}",message);
                    try{
//                        respon = clientHttp.sendHttp (message, url);
                        clientHttp.sendHttp (message,"send");
                        serverSocket = new ServerSocket(port);
                        clientSocket=serverSocket.accept();//establishes connection
                        dis = new DataInputStream(clientSocket.getInputStream());
                        respon = (String)dis.readUTF();

                        serverSocket.close ();
                        clientSocket.close ();
                        dis.close ();
                        logger.info ("Get response Iso Message : {}",respon);

                        isoMsg = decodeIso.parseISOMessage (respon);
                        String statusCode = isoMsg.getString (39);
                        String atasNama = isoMsg.getString (102);
                        int saldo = Integer.parseInt (isoMsg.getString (62));
                        if("00".equalsIgnoreCase (statusCode)){
                            System.out.println ("\t\t Atas Nama: "+atasNama);
                            System.out.println ("\t\t No Rekening: "+accountNumber);
                            System.out.println ("\t\t Nominal: Rp. "+amount);
                            System.out.println ();
                            System.out.println ("Apakah anda yakin?  \t >> YA \n\t\t\t\t\t\t >> TIDAK");
                            System.out.print ("=> ");
                            String jawaban = scanner.next ();
                            if(jawaban.equalsIgnoreCase ("ya")){
                                String newUrl = "payment";
                                message = paymentIso.buildIsoMessagePayment (accountNumber, pinNumber, virtualAccount, amount, server, port);
                                clientHttp.sendHttp (message,"send");
                                serverSocket = new ServerSocket(port);
                                clientSocket=serverSocket.accept();//establishes connection
                                dis = new DataInputStream(clientSocket.getInputStream());
                                respon = (String)dis.readUTF();
                                isoMsg = decodeIso.parseISOMessage (respon);
                                System.out.println ("\t\t Pembayaran Berhasil");
                                logger.info ("Payment Successfully");

                                serverSocket.close ();
                                clientSocket.close ();
                                dis.close ();

                            }
                            break stop;
                        }else if("76".equalsIgnoreCase (statusCode)){
                            System.out.println ("\t\t Nomor Virtual Account Salah");
                            logger.info ("Forwarding Account Number is wrong");
                            serverSocket.close ();
                            clientSocket.close ();
                            dis.close ();
                            break stop;
                        }else if("51".equalsIgnoreCase (statusCode)){
                            System.out.println ();
                            logger.warn ("Insufficient balance remaining");
                            logger.info ("Amount : {}",isoMsg.getString (54));
                            System.out.println ("\t Maaf, Saldo anda tidak mencukupi.");
                            System.out.println ("\t\t No Rekening: "+accountNumber);
                            System.out.println ("\t\t Sisa saldo: Rp. "+saldo);
                            serverSocket.close ();
                            clientSocket.close ();
                            dis.close ();
                            break stop;
                        }else if("05".equalsIgnoreCase (statusCode)) {
                            System.out.println ("AUTENTIKASI GAGAL :(");
                            logger.warn ("Failed authentication");
                            serverSocket.close ();
                            clientSocket.close ();
                            dis.close ();
                            break stop;
                        }
                    }catch (Exception e){
                        logger.error ("Error : {} in method {}",e.getMessage (), e.getStackTrace ());
                    }
                    break;
                default:
                    break;
            }
            System.out.println ("Apakah anda ingin melanjutkan transaksi? \t\t << YA \n  \t\t\t\t\t\t\t\t\t\t\t\t << TIDAK");
            System.out.print ("==> ");
            String pilihan = scanner.next ();
            if(pilihan.equalsIgnoreCase ("YA")){
                System.out.print ("Masukkan Pin: ");
                pinNumber = scanner.next ();
                ans =0;
            }else break;
        }while (ans!=8);
    }

//    public static void autentikasi(){
//        System.out.print ("Masukkan Rekening: ");
//        accountNumber = scanner.next ();
//        System.out.print ("Masukkan Pin: ");
//        pinNumber = scanner.next ();
//    }

    private static Integer nominalTunai(){
        int nominal = 0;
        System.out.println ("1. 50000 \t\t  4. 300000");
        System.out.println ("2. 100000 \t\t  5. 500000");
        System.out.println ("3. 200000 \t\t  6. 1000000");
        System.out.println ("7. Lainnya \t\t  8. Batal");
        System.out.print("=> ");
        String pilihNominal = scanner.next ();
        if(pilihNominal.equalsIgnoreCase ("7")){
            System.out.print ("Masukkan Nominal Kelipatan 50000: ");
            nominal=scanner.nextInt ();
            while (nominal%50000 != 0){
                System.out.println ("Inputan salah");
                System.out.print ("Masukkan Nominal Kelipatan 50000: ");
                nominal=scanner.nextInt ();
            }
        }else if(pilihNominal.equalsIgnoreCase ("1")) nominal=50000;
        else if(pilihNominal.equalsIgnoreCase ("2")) nominal=100000;
        else if(pilihNominal.equalsIgnoreCase ("3")) nominal=200000;
        else if(pilihNominal.equalsIgnoreCase ("4")) nominal=300000;
        else if(pilihNominal.equalsIgnoreCase ("5")) nominal=500000;
        else if(pilihNominal.equalsIgnoreCase ("6")) nominal=1000000;
        else {
            System.out.println ("Error format");
            nominal=0;
        }
        return nominal;
    }
    private static Integer nominalPulsa(){
        int pulsa=0;
        System.out.println ("\t\t Nominal Pulsa Transfer \t\t");
        System.out.println ("\t >> 1. 10000 \t 4. 100000 <<");
        System.out.println ("\t >> 2. 20000 \t 5. 200000 <<");
        System.out.println ("\t >> 3. 50000 \t 6. 500000 <<\n");
        System.out.print ("=> ");
        int pilih = scanner.nextInt ();
        if(pilih==1){
            pulsa=10000;
        }else if(pilih==2){
            pulsa=20000;
        }else if(pilih==3){
            pulsa=50000;
        }else if(pilih==4){
            pulsa=100000;
        }else if(pilih==5){
            pulsa=200000;
        }else if(pilih==6){
            pulsa=500000;
        }

        return pulsa;
    }

    private static void showMenu() {
        System.out.println("\t\t ATM MINI \t\t");
        System.out.println("\tPILIH JENIS TRANSAKSI\n");
        System.out.println("1. TARIK TUNAI"+ "\t\t 5. PEMBAYARAN");
        System.out.println("2. TRANSFER"+"\t\t\t 6. KELUAR");
        System.out.println("3. PEMBELIAN");
        System.out.println("4. INFO SALDO");
        System.out.print("=> ");
    }
}

