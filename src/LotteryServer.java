import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LotteryServer {
    private static final List<ClientHandler> clients = new ArrayList<ClientHandler>();
    private static final List<LotteryObj> lotteries = new ArrayList<>();
    private static final List<UserSerObj> savedUserBets = new ArrayList<>();
    private static final List<LotteryObj> historyData = new ArrayList<>();
    private static Integer poolMoney = 0;

    public static void main(String[] args){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (true){
                        //System.out.println(lotteries);
                        if (!lotteries.isEmpty()){
                            Date date = new Date();
                            for(LotteryObj lottery: lotteries){
                                if (date.getTime() - lottery.getLotteryTime().getTime() >= 0 ){
                                    Random r = new Random();
                                    int low = 1;
                                    int high = 3;
                                    lottery.setWinner(r.nextInt(high-low) + low);
                                    setWinner(lottery);
                                    break;
                                }
                            }
                        }

                        Thread.sleep(1000);
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
        }).start();

        try(ServerSocket serverSocket = new ServerSocket(5000)){
            System.out.println("Listening to prt:5000");

            while(!serverSocket.isClosed()){
                try{
                    Socket connectedSocket = serverSocket.accept();
                    System.out.println(connectedSocket + " Connected \n");
                    ClientHandler newHandler = new ClientHandler(connectedSocket);
                    clients.add(newHandler);
                    Thread thread = new Thread(newHandler);
                    thread.start();

                }catch (Exception e){
                }
            }

        }catch (Exception e){
            System.out.println(e.toString());
        }
    }


    public static void setWinner(LotteryObj lottery){
        Integer amountOfWinners = 0;
        for (UserSerObj user: savedUserBets) {
            for (UserSerObj.UserBets userBet: user.getUserBets()) {
                if (userBet.getTimeSlot().equals(lottery.getLotteryTime())){
                    for (Integer betNumber: userBet.getNumbers()){
                        if (Objects.equals(betNumber, lottery.getWinner())){
                            amountOfWinners++;
                            lottery.addWinnerEmail(user.getUserEmail());
                            System.out.println("Send mail to winner " + user.getUserEmail());
                        }
                    }
                }
            }
        }

        lottery.setPoolMoney(poolMoney);

        if (amountOfWinners > 0){
            lottery.setMoneyPerWinner(lottery.getPoolMoney()/amountOfWinners);
            poolMoney = 0;
        }else{
            poolMoney = lottery.getPoolMoney();
            lottery.setMoneyPerWinner(0);
        }

        historyData.add(lottery);
        lotteries.remove(lottery);
        System.out.println("winner picked on lottery");
    }

    public static class ClientHandler implements Runnable {
        private static ObjectInputStream objIn = null;
        private static ObjectOutputStream objOut = null;
        Socket clientSocket = null;

        public ClientHandler(Socket socket){
            try{
                this.clientSocket = socket;
                objIn = new ObjectInputStream(socket.getInputStream());
                objOut = new ObjectOutputStream(socket.getOutputStream());
            }catch (Exception e){
                System.out.println(e.toString());
            }
        }

        @Override
        public void run() {
            while (clientSocket.isConnected()){
                    try{
                        String chooseString = (String) objIn.readUTF();
                        System.out.println(chooseString);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd-HH:mm");
                        Date betDate;
                        try{
                            betDate = dateFormat.parse(chooseString);
                        }catch (ParseException e){
                            objOut.writeUTF("Something went wrong! ");
                            objOut.flush();
                            throw e;
                        }

                        boolean getBet = false;
                        for (LotteryObj histLottery: historyData) {

                            if (betDate.equals(histLottery.getLotteryTime())){
                                getBet = true;
                                objOut.writeObject(histLottery);
                                objOut.flush();
                            }
                        }
                        if (!getBet){
                            objOut.writeUTF("none :C");
                            objOut.flush();
                        }
                    }catch (Exception e){}

                    try{
                        UserSerObj userBetsObj = (UserSerObj) objIn.readObject();
                        List<UserSerObj.UserBets> userBets = (List<UserSerObj.UserBets>) userBetsObj.getUserBets();
                        for (UserSerObj.UserBets bets : userBets) {
                            boolean updated = false;

                            Date date = new Date();
                            if (date.getTime() - bets.getTimeSlot().getTime() > 0){
                                System.out.println("Date already passed");
                                break;
                            }

                            for (LotteryObj lottery: lotteries) {
                                if (Objects.equals(lottery.getLotteryTime(), bets.getTimeSlot())){
                                    for(UserSerObj savedBets : savedUserBets){
                                        if (savedBets.getUserEmail().equals(userBetsObj.getUserEmail())){
                                            System.out.println("already registersted ");
                                            break;
                                        }
                                    }
                                    updated = true;
                                    for(Integer number : bets.getNumbers()){
                                        lottery.addMoney();
                                    }
                                }
                            }
                            if (!updated){
                                LotteryObj newLottery = new LotteryObj(bets.getTimeSlot());
                                lotteries.add(newLottery);
                                for(Integer number : bets.getNumbers()){
                                    newLottery.addMoney();
                                }

                                System.out.println("added lottory at: " + newLottery.getLotteryTime());
                            }
                        }
                        savedUserBets.add(userBetsObj);

                    }catch (Exception e){
                    }
            }
        }
    }
}