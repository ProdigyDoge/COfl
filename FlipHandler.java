package de.torui.coflsky;

import de.torui.coflsky.commands.models.FlipData;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FlipHandler {

    public static class FlipDataStructure {

        private Map<Long, FlipData> Flips = new ConcurrentHashMap<>();
        private Map<FlipData, Long> ReverseMap = new ConcurrentHashMap<>();

        private FlipData HighestFlip = null;
        private FlipData LastFlip = null;

        private Timer t = new Timer();
        private TimerTask CurrentTask = null;

        public synchronized void RunHouseKeeping() {
            synchronized (Flips) {

                Long RemoveAllPrior = System.currentTimeMillis() - (Config.KeepFlipsForSeconds * 1000);
                Flips.keySet().stream().filter(l -> l <= RemoveAllPrior).forEach(l -> RemoveLong(l));
                if (!Flips.isEmpty()) {
                    HighestFlip = Flips.values().stream().max((f1, f2) -> f1.Worth - f2.Worth).orElse(null);
                } else {
                    HighestFlip = null;
                }
            }

            if (CurrentTask != null) {
                CurrentTask.cancel();
                CurrentTask = null;
                t.purge();
            }
            if (!Flips.isEmpty()) {
                CurrentTask = new TimerTask() {
                    @Override
                    public void run() {
                        RunHouseKeeping();
                    }
                };
                t.schedule(CurrentTask, Config.KeepFlipsForSeconds * 1000 + /* small arbitrary delay */150);
            }
        }

        public synchronized void Insert(FlipData flip) {
            Long l = System.currentTimeMillis();
            LastFlip = flip;

            synchronized (Flips) {
                Flips.put(l, flip);
                ReverseMap.put(flip, l);
            }

            RunHouseKeeping();
        }

        private void RemoveLong(Long l) {
            if (l == null)
                return;
            synchronized (Flips) {
                FlipData f = Flips.get(l);
                if (f != null) {
                    ReverseMap.remove(f);
                    Flips.remove(l);
                }
            }
        }

        private void RemoveFlip(FlipData f) {
            if (f == null)
                return;

            synchronized (Flips) {
                Long l = ReverseMap.get(f);
                if (l != null) {
                    Flips.remove(l);
                    ReverseMap.remove(f);
                }
            }
        }

        public FlipData GetHighestFlip() {
            return HighestFlip;
        }

        public FlipData GetLastFlip() {
            if (LastFlip == null) {
                return null;
            }
            Long l = ReverseMap.get(LastFlip);
            if (l == null) {
                LastFlip = null;
            }
            return LastFlip;
        }

        public FlipData getFlipById(String id) {
            FlipData[] flips = Flips.values().stream().filter(flipData -> flipData.Id.equals(id)).toArray(FlipData[]::new);
            if (flips.length == 0) {
                return null;
            }
            return flips[0];
        }

        public void InvalidateFlip(FlipData flip) {
            RemoveFlip(flip);
            RunHouseKeeping();
        }

        public int CurrentFlips() {
            return Flips.size();
        }

    }

    public FlipDataStructure fds;
    public String lastClickedFlipMessage;

    public FlipHandler() {
        fds = new FlipDataStructure();
    }

}

// 
package de.torui.coflsky;

import de.torui.coflsky.commands.models.FlipData;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FlipHandler {

    public static class FlipDataStructure {

        private Map<Long, FlipData> Flips = new ConcurrentHashMap<>();
        private Map<FlipData, Long> ReverseMap = new ConcurrentHashMap<>();

        private FlipData HighestFlip = null;
        private FlipData LastFlip = null;

        private Timer t = new Timer();
        private TimerTask CurrentTask = null;

        public synchronized void RunHouseKeeping() {
            synchronized (Flips) {

                Long RemoveAllPrior = System.currentTimeMillis() - (Config.KeepFlipsForSeconds * 1000);
                Flips.keySet().stream().filter(l -> l <= RemoveAllPrior).forEach(l -> RemoveLong(l));
                if (!Flips.isEmpty()) {
                    HighestFlip = Flips.values().stream().max((f1, f2) -> f1.Worth - f2.Worth).orElse(null);
                } else {
                    HighestFlip = null;
                }
            }

            if (CurrentTask != null) {
                CurrentTask.cancel();
                CurrentTask = null;
                t.purge();
            }
            if (!Flips.isEmpty()) {
                CurrentTask = new TimerTask() {
                    @Override
                    public void run() {
                        RunHouseKeeping();
                    }
                };
                t.schedule(CurrentTask, Config.KeepFlipsForSeconds * 1000 + /* small arbitrary delay */150);
            }
        }

        public synchronized void Insert(FlipData flip) {
            Long l = System.currentTimeMillis();
            LastFlip = flip;

            synchronized (Flips) {
                Flips.put(l, flip);
                ReverseMap.put(flip, l);
            }

            RunHouseKeeping();
        }

        private void RemoveLong(Long l) {
            if (l == null)
                return;
            synchronized (Flips) {
                FlipData f = Flips.get(l);
                if (f != null) {
                    ReverseMap.remove(f);
                    Flips.remove(l);
                }
            }
        }

        private void RemoveFlip(FlipData f) {
            if (f == null)
                return;

            synchronized (Flips) {
                Long l = ReverseMap.get(f);
                if (l != null) {
                    Flips.remove(l);
                    ReverseMap.remove(f);
                }
            }
        }

        public FlipData GetHighestFlip() {
            return HighestFlip;
        }

        public FlipData GetLastFlip() {
            if (LastFlip == null) {
                return null;
            }
            Long l = ReverseMap.get(LastFlip);
            if (l == null) {
                LastFlip = null;
            }
            return LastFlip;
        }

        public FlipData getFlipById(String id) {
            FlipData[] flips = Flips.values().stream().filter(flipData -> flipData.Id.equals(id)).toArray(FlipData[]::new);
            if (flips.length == 0) {
                return null;
            }
            return flips[0];
        }

        public void InvalidateFlip(FlipData flip) {
            RemoveFlip(flip);
            RunHouseKeeping();
        }

        public int CurrentFlips() {
            return Flips.size();
        }

    }

    public FlipDataStructure fds;
    public String lastClickedFlipMessage;

    public FlipHandler() {
        fds = new FlipDataStructure();
    }

}

// This Java code defines a FlipHandler class with a nested FlipDataStructure class. The FlipHandler is responsible for managing a collection of "flips," likely representing some form of trade or transaction. Here's a breakdown of what each part of the code does: FlipHandler Class
// Fields:
// fds: An instance of the nested FlipDataStructure class.
// lastClickedFlipMessage: A string field, likely used to store a message related to the last flip that was clicked.
// Constructor:
// FlipHandler(): Initializes a new FlipDataStructure instance and assigns it to fds.
// FlipDataStructure Class
// This nested class is responsible for storing and managing the flips.

// Fields:
// Flips: A concurrent hash map storing FlipData objects indexed by a Long timestamp.
// ReverseMap: A concurrent hash map for reverse lookup, storing timestamps indexed by FlipData objects.
// HighestFlip: Stores the FlipData object with the highest value (worth).
// LastFlip: Stores the most recently inserted FlipData object.
// t: A Timer object for scheduling housekeeping tasks.
// CurrentTask: A TimerTask object representing the currently scheduled housekeeping task.
// Methods:
// RunHouseKeeping():

// Removes flips older than a certain threshold.
// Updates HighestFlip to the flip with the highest worth.
// Cancels the current housekeeping task if there is one.
// Schedules a new housekeeping task if there are any flips left.
// Insert(FlipData flip):

// Inserts a new flip into the Flips map.
// Updates LastFlip to the newly inserted flip.
// Schedules the housekeeping task by calling RunHouseKeeping().
// RemoveLong(Long l):

// Removes a flip from both Flips and ReverseMap based on the timestamp.
// RemoveFlip(FlipData f):

// Removes a flip from both Flips and ReverseMap based on the FlipData object.
// GetHighestFlip():

// Returns the flip with the highest worth.
// GetLastFlip():

// Returns the most recently inserted flip, or null if there are no flips.
// getFlipById(String id):

// Returns a flip based on its ID, or null if no flip matches the given ID.
// InvalidateFlip(FlipData flip):

// Removes a specific flip and runs housekeeping.
// CurrentFlips():

// Returns the current number of flips.
// Functionality Overview
// Managing Flips: The class provides methods to insert, remove, and retrieve flips. Flips are stored with timestamps for easy management.
// Housekeeping: Regular housekeeping tasks are scheduled to remove old flips and update the highest worth flip.
// Concurrency: Uses concurrent hash maps to handle concurrent access to the flips, making it thread-safe.
// Utilities: Provides utility methods to get the highest worth flip, the most recent flip, and flips by ID.
// This class is designed to manage a collection of FlipData objects efficiently, ensuring that outdated flips are periodically removed and that various utility methods can be used to interact with the collection.
