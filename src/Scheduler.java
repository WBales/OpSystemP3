import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Scheduler {

    public static void main(String[] args){
        Scheduler scheduler = new Scheduler();
        File file = new File("C:\\Users\\Wesley\\IdeaProjects\\OpSystemsProject3\\src\\jobs.txt");
        ArrayList<Program> programArray = new ArrayList<Program>(); //Hold a variable number of programs from input file
        int totalDuration = 0;                                //Keeps track of the total time to run all programs. Used to hold the order the programs run
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while((line = br.readLine()) != null){                  //Read input file
                String[] lineContents = line.split("\\t+");
                if(lineContents[1].matches("\\d+") && lineContents[2].matches("\\d+")){
                    Program temp = new Program(lineContents[0], Integer.parseInt(lineContents[1]), Integer.parseInt(lineContents[2]));
                    programArray.add(temp);
                    totalDuration = totalDuration + temp.getDuration();
                }
            }

            String[] command = args[0].split("\\s+");
            String[]programOrder = new String[totalDuration];       // May take out. Allows user to enter some list of programs to run

            for (String scheduleOption: command){
                switch(scheduleOption){
                    case "FCFS":
                        scheduler.firstComeFirstServe(programOrder, programArray);
                        break;
                    case "RR":
                        scheduler.roundRobin(programOrder, scheduler.copyPrograms(programArray));
                        break;
                    case "SPN":
                        scheduler.shortestProcessNext(programOrder, scheduler.copyPrograms(programArray));
                        break;
                    case "SRT":
                        scheduler.shortestRemainingTime(programOrder, scheduler.copyPrograms(programArray));
                        break;
                    case "HRRN":
                        scheduler.highestResponseRatioNext(programOrder, scheduler.copyPrograms(programArray));
                        break;
                    case "FB":
                        scheduler.multiLevelFeedback(programOrder, scheduler.copyPrograms(programArray));
                        break;
                    case "ALL":
                        scheduler.firstComeFirstServe(programOrder, programArray);
                        scheduler.roundRobin(programOrder, scheduler.copyPrograms(programArray));
                        scheduler.shortestProcessNext(programOrder, scheduler.copyPrograms(programArray));
                        scheduler.shortestRemainingTime(programOrder, scheduler.copyPrograms(programArray));
                        scheduler.highestResponseRatioNext(programOrder, scheduler.copyPrograms(programArray));
                        scheduler.multiLevelFeedback(programOrder, scheduler.copyPrograms(programArray));
                        break;
                    default:
                        System.out.println(scheduleOption + " is not a valid option.");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public ArrayList<Program> copyPrograms(ArrayList<Program> programArray){
        ArrayList<Program> temp = new ArrayList<>();
        for(Program program : programArray){
            temp.add(program.deepCopy());
        }
        return temp;
    }

    public void printName(String name, int length){
        System.out.println(name);
        for(int i = 0; i <= (length) ; i++){
            System.out.print("--");
        }
        System.out.println();
    }

    public void printOrder(String[] programOrder, ArrayList<Program> programArray){
        String line;
        for (Program program: programArray){
            line = (program.getName() + " ");
            for(int i = 0; i < programOrder.length; i++){
                if(programOrder[i].equals(program.getName())){
                    line = (line + "X ");
                } else {
                    line = (line + "  ");
                }
            }
            System.out.println(line);
        }
        System.out.println();
    }

    private Program getShortestProgram(ArrayList<Program> programArray, int numPrograms, Program currentProgram) {
        //Returns the shortest program that still needs to run
        for(int i = 0; i < numPrograms; i++){
            if(programArray.get(i).getDuration() != 0){
                if(currentProgram.getDuration() == 0){
                    currentProgram = programArray.get(i);
                }

                if(currentProgram.getDuration() > programArray.get(i).getDuration()){
                    currentProgram = programArray.get(i);
                }
            }
        }
        return currentProgram;
    }

    public void runProgram(String[] programOrder, int time, Program currentProgram){
        programOrder[time] = currentProgram.getName();
        currentProgram.setDuration(currentProgram.getDuration() - 1);
    }

    public void firstComeFirstServe(String[] programOrder, ArrayList<Program> programArray){
        int time = 0;
        for (Program program : programArray) {                  //Goes down the list and runs each program to completion as they were received
            for(int i = 0; i < program.getDuration(); i++){
                programOrder[time] = program.getName();
                time++;
            }
        }
        printName("First Come First Serve", programOrder.length);
        printOrder(programOrder, programArray);
    }

    public void roundRobin(String[] programOrder, ArrayList<Program> programArray){
        int quantum = 1;
        Queue<Program> rrQueue = new LinkedList<>();
        int numPrograms = 1;
        Program currentProgram = null;
        //Prime Queue
        rrQueue.add(programArray.get(0));//First Program
        for(int time = 0; time < programOrder.length; time++){
            if(numPrograms < programArray.size()){
                if(programArray.get(numPrograms).getStartTime() == (time+1)) {  //Simulates program arrival
                    rrQueue.add(programArray.get(numPrograms));
                    numPrograms++;
                }
            }
            for(int i = 0; i < quantum; i++){                                   //Run program
                currentProgram = rrQueue.remove();
                runProgram(programOrder, time, currentProgram);
            }
            if(currentProgram.getDuration() != 0){                              //Add to end of queue
                rrQueue.add(currentProgram);
            }
        }
        printName("Round-Robin", programOrder.length);
        printOrder(programOrder, programArray);
    }

    public void shortestProcessNext(String[] programOrder, ArrayList<Program> programArray){
        int numPrograms = 1;
        Program currentProgram = null;

        for(int time = 0; time < programOrder.length; time++){
            if(time == 0){
                currentProgram = programArray.get(0);
            }

            if(numPrograms < programArray.size()){                                  //Simulates program arrival
                if(programArray.get(numPrograms).getStartTime() == (time)) {
                    numPrograms++;
                }
            }

            if(currentProgram.getDuration() == 0){                                  //Gets shortest available program when one has previously ended
                currentProgram = getShortestProgram(programArray, numPrograms, currentProgram);
            }

            runProgram(programOrder, time, currentProgram);
        }
        printName("Shortest Process Next", programOrder.length);
        printOrder(programOrder, programArray);
    }

    public void shortestRemainingTime(String[] programOrder, ArrayList<Program> programArray){
        int numPrograms = 1;
        Program currentProgram = null;

        for(int time = 0; time < programOrder.length; time++){
            if(time == 0){
                currentProgram = programArray.get(0);
            }

            if(numPrograms < programArray.size()){
                if(programArray.get(numPrograms).getStartTime() == (time)) {                //Simulates Arrival
                    numPrograms++;
                }
            }

            currentProgram = getShortestProgram(programArray, numPrograms, currentProgram); //Shortest program at each time interval

            runProgram(programOrder, time, currentProgram);
        }
        printName("Shortest Remaining time", programOrder.length);
        printOrder(programOrder, programArray);
    }

    public void highestResponseRatioNext(String[] programOrder, ArrayList<Program> programArray){
        int numPrograms = 1;
        Program currentProgram = null;

        for(int time = 0; time < programOrder.length; time++){
            if(time == 0){
                currentProgram = programArray.get(0);
            }

            if(numPrograms < programArray.size()){                              //Simulate arrival
                if(programArray.get(numPrograms).getStartTime() == (time)) {
                    numPrograms++;
                }
            }

            if(currentProgram.getDuration() == 0){
                for(int i = 0; i < programArray.size(); i++){
                    if(programArray.get(i).getDuration() != 0){
                        if(currentProgram.getDuration() == 0){                  //First pass finding a program that hasn't been run
                            currentProgram = programArray.get(i);
                        }
                        if(currentProgram.getDuration() != 0){                  //Comparing programs that can run to find the highest response ratio
                            Program temp = programArray.get(i);
                            int tempResponseRatio = (((time - temp.getStartTime()) + temp.getDuration())/temp.getDuration());
                            int currentResponseRatio = (((time - currentProgram.getStartTime()) + currentProgram.getDuration())/ currentProgram.getDuration());

                            if(tempResponseRatio > currentResponseRatio){
                                currentProgram = programArray.get(i);           //Finding the highest ratio
                            }
                        }
                    }
                }
            }
            runProgram(programOrder, time, currentProgram);
        }
        printName("Highest Response Ratio Next", programOrder.length);
        printOrder(programOrder, programArray);
    }

    public void multiLevelFeedback(String[] programOrder, ArrayList<Program> programArray){
        Queue<Program> topQueue = new LinkedList<>();
        Queue<Program> middleQueue = new LinkedList<>();
        Queue<Program> bottomQueue = new LinkedList<>();

        int quantum = 1;
        int numPrograms = 0;
        Program currentProgram = null;

        int lastQueue = 0;

        for(int time = 0; time < programOrder.length; time++){

            if(numPrograms < programArray.size()){                              //Rolling time check that simulates program arrival
                if(programArray.get(numPrograms).getStartTime() == (time)) {
                    topQueue.add(programArray.get(numPrograms));
                    numPrograms++;
                }
            }

            if(topQueue.size() != 0 || middleQueue.size() != 0 || bottomQueue.size() != 0){ //Edge case for no additional programs to run, allows the current program to keep running
                if(currentProgram != null && currentProgram.getDuration() != 0){
                    if(lastQueue == 0){
                        middleQueue.add(currentProgram);
                    } else if(lastQueue == 1 || lastQueue == 2){
                        bottomQueue.add(currentProgram);
                    }
                }
            }

            if(topQueue.size() != 0){                           //Top               //Runs programs prioritizing down the queues
                currentProgram = topQueue.remove();
                lastQueue = 0;
            } else if(middleQueue.size() != 0){                 //Middle
                currentProgram = middleQueue.remove();
                lastQueue = 1;
            } else if(bottomQueue.size() != 0){                 //Bottom
                currentProgram = bottomQueue.remove();
                lastQueue = 2;
            }

            //Simulate program running on the interval of the quantum
            for(int i = 0; i < quantum; i++){
                runProgram(programOrder, time, currentProgram);
            }
        }
        printName("Multilevel Feedback", programOrder.length);
        printOrder(programOrder, programArray);
    }
}
