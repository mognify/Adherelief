// finished: gathering the information from paste
// next: create timers
// will use https://vclock.com/#time=17:59&title=Alarm&sound=glow&loop=1
// for the alarms
// need to create getPaste()
// (need to test still)
// i also want this program to open up other applications
// service-now, remote tool, itsd tools, slack, 
// http://webapps.homedepot.com/itsd/dashboard/
// https://thdengops.slack.com/
// https://workforce.homedepot.com/wfo/control/showadherence
// http://webapps.homedepot.com/itsd/dashboard/
// TODO: actually, i want the user to paste these into a text file and have them open from there

import java.awt.Desktop;
import java.net.URI;
import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;

public class Easy {
 public static final boolean debug = true;
 // automatically creates alarms from pasted schedule

 public static void main(String[] args) {
  final boolean methodA = true;

  // day, lunch time(s), breaks\
  HashMap < String, int[] > schedule = new HashMap < String, int[] > ();
  String[] input = getPaste();

  for (int i = 0; i < input.length; i++) {
   String day = "";
   int lunch = 0,
    breaks[] = new int[] {
     0,
     0
    };
   int b = 0;
   outln("Variables initialized: day, lunch, breaks");

   if (input[i].contains("Monday|Tuesday|Wednesday|Thursday|Friday")) {
    day = input[i].trim().substring(0, 6);
    outln(day + " identified.");
   }

   if (input[i].contains("Break")) {
    b++;
    outln("Break " + String.valueOf(b) + " identified.");
    breaks[b] = getBreak(input[i], false);
   } else if (input[i].contains("Lunch")) {
    outln("Lunch identified.");
    lunch = getBreak(input[i], true);
    outln("Lunch found to be at " +
     String.valueOf(lunch / 60) + ":" + String.valueOf(lunch % 60));
   }

   schedule.put(day, new int[] {
    breaks[0], lunch,
     breaks[1]
   });

   // TODO: iterate through map and open browser windows
   Iterator itr8r = schedule.entrySet().iterator();
   while (itr8r.hasNext()) {
    Map.Entry pair = (Map.Entry) itr8r.next();
    String day = pair.getKey();
    int[] times = pair.getValue(); // break1, lunch, break2
    outln(pair.getKey() + " = " + pair.getValue());
    for (int i = 0; i < times.length(); i++) {
     if (i % 2 != 0) {
      browserTimer("Break", times[i]);
      browserTimer("BrEnd", times[i] + 15);
     } else {
      browserTimer("Lunch", times[i]);
      browserTimer("LuEnd", times[i] + 60);
     }
    }
    itr8r.remove();
   }
  }
 }

 static private String[] getPaste() {
  outln("getPaste() begin");
  Scanner inputScanner = new Scanner(System.in);
  outln("Variable initialized: inputScanner");
  outln("Variable declared: inputScanner as new Scanner object");

  System.out.println("How many lines?");
  String[] s = new String[inputScanner.nextInt()];

  System.out.println("Paste the CtrlA schedule");
  for (int i = 0; i < s.length; i++) {
   s[i] = inputScanner.nextLine();
  }
  inputScanner.close();
  outln("Variable changed: inputScanner closed");
  outln("Finished retrieving lines.");
  outln("getPaste() end");
  return s;
 }

 // time will be stored in minutes
 static private int getBreak(String s, boolean lunch) {
  outln("getBreak() start");
  int breakTime = 0;
  outln("Variable initialized: breakTime");

  String work = lunch ? s.split("h", 2)[1] : s.split("k", 1)[1];
  outln("Variable initialized: work");
  outln("Variable changed: work to " + work);
  breakTime += (60 * Integer.valueOf(work.split(":")[0]).intValue()) +
   Integer.valueOf(work.split(":")[1]).intValue();
  outln("Variable changed: breakTime to " +
   String.valueOf(breakTime));
  if (s.split(":", 2)[1].contains("PM")) {
   outln("PM identified, adding 12*60 minutes");
   breakTime += 12 * 60;
  }
  outln("getBreak() end");
  return breakTime;
 }

 static private void outln(String s) {
  if (debug)
   System.out.println("DEBUG: " + s);
 }

 static public void browserTimer(String name, int time /*, boolean x*/ ) {
  // todo: add break ends
  String site = "https://vclock.com/#time=[a]&title=" + name + "&sound=glow&loop=1";
  String a = String.valueOf(time / 60) + ":" + String.valueOf(time % 60);
  if (a.length() < 5) a = "0" + a;
  site = site.replace("[a]", a); // add the break time into the alarm
  Runtime.getRuntime().exec(new String[] {
   "cmd",
   "/c",
   "start chrome " + site
  });

  String a = String.valueOf(time / 60) + ":" + String.valueOf(time % 60);
  if (a.length() < 5) a = "0" + a;
  site = site.replace("[a]", a); // add the break name into the alarm
  Runtime.getRuntime().exec(new String[] {
   "cmd",
   "/c",
   "start chrome " + site
  });
  /*if(x)
  if(name.contains("B")){ // 15 min break
   browserTimer
  }else{ // lunch
   
  }*/
 }
}
