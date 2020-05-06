package server;

import java.util.Comparator;

public class ScoreSorter implements Comparator<UserResult> {
	 @Override
	    public int compare(UserResult o1, UserResult o2) {
	        return o2.getScore() - o1.getScore();
	    }
}
