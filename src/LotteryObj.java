import java.io.Serializable;
import java.util.*;

public class LotteryObj implements Serializable{
    private Date timeSlot;
    private Integer poolMoney;
    private Integer winner;
    private Integer moneyPerWinner;
    private List<String> usersWon = new ArrayList<>();

    public LotteryObj(Date inputTime){
        timeSlot = inputTime;
        poolMoney = 0;
        winner = 0;
        this.moneyPerWinner = 0;
    }

    public Date getLotteryTime(){
        return timeSlot;
    }

    public Integer getWinner(){
        return  winner;
    }

    public Integer getPoolMoney(){
        return poolMoney;
    }

    public void setPoolMoney(Integer bonus){
        poolMoney += bonus;
    }

    public String historyLotteryInfo(){
        return "Winnernumber: " + getWinner() + ", Poolmoney: " + getPoolMoney() + ", WinnerEmails:" + getUsersWonEmail() + ", Win for each user: " + moneyPerWinner;
    }

    public List<String> getUsersWonEmail(){
        return usersWon;
    }

    public void addWinnerEmail(String userEmail) {
        usersWon.add(userEmail);
    }

    public void setMoneyPerWinner(Integer moneyPerWinner) {
        this.moneyPerWinner = moneyPerWinner;
    }

    public void setWinner(Integer winner){
        this.winner =winner;
    }

    public void addMoney(){
        this.poolMoney += 100;
    }

    public void takeSlot(Integer number){
        poolMoney += 100;
    }

    public String getLotteryInfo(){
        return "Time: " + timeSlot.toString() + ", Choose between: 0..255, ";
    }
}
