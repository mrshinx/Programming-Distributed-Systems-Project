package Game;

import java.util.*;

public class Record {

    String account;
    Double time;

    public Record(String account, Double time)
    {
        this.account = account;
        this.time = time;
    }

    public Double getTime()
    {
        return time;
    }

    @Override
    public String toString() {
        return "Account= " + account + " Time= " +time;
    }

    static class CustomerSortingComparator implements Comparator<Record>
    {

        @Override
        public int compare(Record record1, Record record2)
        {

            // for comparison
            int TimeCompare = record1.getTime().compareTo(record2.getTime());
            return TimeCompare;
        }
    }

    public static void main(String[] args) {

        // create ArrayList to store Student
        List<Record> al = new ArrayList<>();

        // create customer objects using constructor initialization
        Record obj1 = new Record("mrshinx",3.5);
        Record obj2 = new Record("mrshinx2",7.0);
        Record obj3 = new Record("mrshinx3",2.5);
        Record obj4 = new Record("mrshinx4",1.2);
        Record obj5 = new Record("mrshinx5",3.5);
        Record obj6 = new Record("mrshinx6",5.3);


        // add customer objects to ArrayList
        al.add(obj1);
        al.add(obj2);
        al.add(obj3);
        al.add(obj4);
        al.add(obj5);
        al.add(obj6);

        // before Sorting arraylist: iterate using Iterator
        Iterator<Record> custIterator = al.iterator();

        System.out.println("Before Sorting:\n");
        while (custIterator.hasNext()) {
            System.out.println(custIterator.next());
        }

        // sorting using Collections.sort(al, comparator);
        Collections.sort(al, new Record.CustomerSortingComparator());

        // after Sorting arraylist: iterate using enhanced for-loop
        System.out.println("\n\nAfter Sorting:\n");
        for (Record record : al) {
            System.out.println(record);
        }
    }

}
