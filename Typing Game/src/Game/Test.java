package Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main (String[] args)
    {
        Map<String, ArrayList<String>> test = new HashMap<>();
        test.put("a",new ArrayList<String>(Arrays.asList("b","c")));
        test.put("d",new ArrayList<String>(Arrays.asList("e","f")));
        System.out.println(test.keySet().toArray()[0]);
        System.out.println(test.keySet().toArray()[1]);
        System.out.println(test.get(test.keySet().toArray()[0]).toArray()[0]);
        System.out.println(test.get(test.keySet().toArray()[0]).toArray()[1]);
    }


}
