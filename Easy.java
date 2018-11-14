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
// TODO: SPECIAL CASE: OFF DAYS

import java.io.IOException;
import java.awt.Desktop;
import java.net.URI;

import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.ArrayList;

public class Easy {
 public static final boolean debug = true;
 // automatically creates alarms from pasted schedule

 public static void main(String[] args) {
  final boolean methodA = true;

  // day, lunch time(s), breaks\
  HashMap < String, Integer[] > schedule = new HashMap < String, Integer[] > ();
  String[] input = getPaste();

  String day = "";
  int lunch = 0;
  Integer[] breaks = new Integer[] {
   0,
   0
  };
  outln("Variables initialized: day, lunch, breaks");

  for (int i = 0; i < input.length; i++) {
   int b = 0;
   outln("Variables initialized: b");

   String dayTemp = getDay(input[i]);
   if (!dayTemp.equals("[BAD]")) {
    day = dayTemp;
    outln("Variable changed: day to " + day);
    continue;
   }

   if (input[i].contains("Break")) {
    b++;

    outln("\tBreak " + String.valueOf(b) + " identified: " + input[i]);

    breaks[b] = getBreak(input[i], false);
   } else if (input[i].contains("Lunch")) {
    outln("\tLunch identified: " + input[i]);
    lunch = getBreak(input[i], true);

    outln("Lunch found to be at " +
     String.valueOf(lunch / 60) + ":" + String.valueOf(lunch % 60));
   } else {
    continue;
   }

   schedule.put(day, new Integer[] {
    breaks[0], lunch,
     breaks[1]
   });

   openBrowserWindows(schedule.entrySet().iterator());

  }
 }

 static private void openBrowserWindows(Iterator << ? > itr8r) {
  outln("openBrowserWindows start");
  while (itr8r.hasNext()) {
   Map.Entry < String, Integer[] > pair = (Map.Entry < String, Integer[] > ) itr8r.next();
   String dayy = pair.getKey(); // CHECK
   if (dayy.contains("[BAD]")) return;
   Integer[] times = pair.getValue(); // break1, lunch, break2
   outln(pair.getKey() + " = " + pair.getValue()[0]);

   for (int j = 0; j < times.length; j++) {
    if (j % 2 != 0) {
     outln("Break" + j + ": " + times[j]);
     browserTimer("Break", times[j]);
     outln("BrEnd" + j + ": " + times[j] + 15);
     browserTimer("BrEnd", times[j] + 15);
    } else {
     outln("Lunch" + j + ": " + times[j]);
     browserTimer("Lunch", times[j]);
     outln("LuEnd" + j + ": " + times[j] + 60);
     browserTimer("LuEnd", times[j] + 60);
    }
   }
  }

  outln("openBrowserWindows end");
  itr8r.remove();

  return;
 }

 static private String getDay(String input) {
  if (input.contains("Monday|Tuesday|Wednesday|Thursday|Friday")) {
   String day = input.trim().substring(0, 6);

   outln(day + " identified.");

   return day;
  }

  return "[BAD]";
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

  //String work = lunch ? s.split("h", 2)[1] : s.split("k", 1)[1];
  String work = lunch ? s.substring(s.indexOf('h') + 1) : s.substring(s.indexOf('k') + 1);

  outln("Variable initialized: work");
  outln("Variable changed: work to " + work);
  String fix1 = work.split(":")[0];
  fix1 = fix1.contains(" ") ? fix1.split(" ")[1] : fix1;
  breakTime += (60 * Integer.valueOf(fix1).intValue()) +
   Integer.valueOf(work.split(":")[1].split(" ")[0]).intValue();
  outln("Variable changed: breakTime to " +
   String.valueOf(breakTime));
  if (s.split(":", 2)[1].contains("PM")) {
   outln("PM identified, adding 12*60 minutes");
   breakTime += 12 * 60;
   outln("Variable changed: breakTime to " + breakTime);
  }
  outln("getBreak() end");
  return breakTime;
 }

 static private void outln(String s) {
  if (debug)
   System.out.println("DEBUG: " + s);
 }

 static public void browserTimer(String name, Integer time /*, boolean x*/ ) {
  outln("browserTimer start");
  String site = "\"https://vclock.com/#time=[a]&title=" + name + "&sound=glow&loop=1\"";
  String a = String.valueOf(time / 60) + ":" + String.valueOf(time % 60);

  if (a.length() < 5) a = "0" + a;
  if (a.length() < 5) a = a + "0";

  site = site.replace("[a]", a); // add the break time into the alarm
  outln("browserTimer: Trying " + site);

  try {
   Runtime.getRuntime().exec(new String[] {
    "cmd",
    "/c",
    "start chrome " + site
   });
  } catch (IOException e) {
   e.printStackTrace();
  }
  try {
   TimeUnit.SECONDS.sleep(3);
  } catch (InterruptedException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }

  outln("browserTimer end");
  return;
 }
}
