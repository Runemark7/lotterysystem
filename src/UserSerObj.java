import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UserSerObj implements Serializable {
    private String userEmail;
    private List<UserBets> userBets = new ArrayList<>();

    public UserSerObj(String userEmail){
        this.userEmail = userEmail;
    }

    public void addBet(Date date, Integer number){
        UserBets userBet = new UserBets(date, number);
        userBets.add(userBet);
    }

    public String getUserEmail() {
        return userEmail;
    }

    public List<UserBets> getUserBets() {
        return userBets;
    }

    public String toString(){
        return "UserSerObj[userEmail=" + userEmail + "]";
    }

    public static class UserBets implements Serializable{
        private List<Integer> numbers = new ArrayList<>();
        private Date timeSlot;

        public UserBets(Date timeSlot, Integer number){
            this.timeSlot = timeSlot;
            numbers.add(number);
        }

        public List<Integer> getNumbers() {
            return numbers;
        }

        public void setNumbers(Integer number) {
            numbers.add(number);
        }

        public Date getTimeSlot(){
            return timeSlot;
        }
    }
}
