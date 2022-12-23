import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class LotteryClient {
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
    private static Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {
        try(Socket socket = new Socket("localhost",5000)){
            ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(System.in));

            while (socket.isConnected()){

                System.out.println("Choose option: 1.Make bet, 2. History  \n");
                String option = br.readLine();

                if (Objects.equals(option, "1")){
                    System.out.println("Choose email: example(bla@gmail.com) \n");
                    String userEmail = br.readLine();

                    System.out.println("Write date you want to bet: yyMMdd-HH:mm \n");
                    String userDate = br.readLine();
                    Date betDate = new SimpleDateFormat("yyMMdd-HH:mm").parse(userDate);

                    UserSerObj returnUser = new UserSerObj(userEmail);

                    System.out.println("Write 'done' when done making bets: ");
                    List<Integer> pickedNumbers = new ArrayList<>();
                    while (true){
                        System.out.println("Choose number: ");
                        String userNumber = br.readLine();
                        if (Objects.equals(userNumber, "done")){
                            break;
                        }
                        Integer bettingNumber = Integer.parseInt(userNumber);
                        if (bettingNumber < 256 && bettingNumber > 0){
                            if(!pickedNumbers.contains(bettingNumber)){
                                returnUser.addBet(betDate, Integer.parseInt(userNumber));
                                pickedNumbers.add(bettingNumber);
                            }else{
                                System.out.println("Number already exists :C \n");
                            }
                        }else{
                            System.out.println("Only numbers between 0-255 \n");
                        }
                    }

                    objOut.writeObject(returnUser);
                    objOut.flush();
                }else if (Objects.equals(option, "2")){
                    System.out.println("Choose date: yyMMdd-HH \n");
                    String choosedDate = br.readLine();

                    objOut.writeUTF(choosedDate);
                    objOut.flush();

                    while (true) {
                        try{
                            String string = (String) objIn.readUTF();
                            System.out.println(string);
                            break;
                        }catch (Exception e){
                        }

                        try{
                            LotteryObj lotteryObj = (LotteryObj) objIn.readObject();
                            System.out.println("doing my best :/");
                            Integer pool = lotteryObj.getPoolMoney();
                            System.out.println(lotteryObj.historyLotteryInfo());
                            break;
                        }catch (Exception e){
                        }
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
}
