// Name: Christopher Carmona

package main.csp;

import java.time.LocalDate;
import java.util.*;

/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some set of unary and binary 
 * constraints on the dates of each meeting.
 */
public class CSP_BACKUP {

    /**
     * Public interface for the CSP solver in which the number of meetings,
     * range of allowable dates for each meeting, and constraints on meeting
     * times are specified.
     * @param nMeetings The number of meetings that must be scheduled, indexed from 0 to n-1
     * @param rangeStart The start date (inclusive) of the domains of each of the n meeting-variables
     * @param rangeEnd The end date (inclusive) of the domains of each of the n meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of the n meetings,
     *         indexed by the variable they satisfy, or null if no solution exists.
     */
    public static List<LocalDate> solve (int nMeetings, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) {

        List<Meeting> meetingsAssignmentList = getMeetingsAssignmentList(nMeetings, rangeStart, rangeEnd, constraints);
        List<Meeting> nodeConsistencyCheck = NodeConsistency(meetingsAssignmentList, constraints);
        if (nodeConsistencyCheck == null) {
            return null;
        }
        List<Meeting> arcConsistencyCheck = ArcConsistency(nodeConsistencyCheck, constraints);
        if (arcConsistencyCheck == null) {
            return null;
        }
        return BacktrackingSearch(meetingsAssignmentList, constraints);
    }


    /**
     *
     * @param nMeetings
     * @param rangeStart
     * @param rangeEnd
     * @param constraints
     * @return
     */
    private static List<Meeting> getMeetingsAssignmentList(int nMeetings, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) {

        // Appends new meeting objects into a meetings list:
        List<Meeting> meetingsList = new ArrayList<>();
        for (int meetingID = 0; meetingID < nMeetings; meetingID++) {
            meetingsList.add( new Meeting(meetingID, rangeStart, rangeEnd, constraints) ); // NOTE: need to adjust contraints here...
        }
        return meetingsList;
    }


    /**
     *
     * @param meetingAssignmentList
     * @return
     */
    private static boolean checkForNull (List<Meeting> meetingAssignmentList) {

        boolean hasNull = false;
        for (Meeting curr : meetingAssignmentList) {
            if (curr.meetingDate == null) {
                hasNull = true;
                break; // Just breaks to save time... no need to check further if any "meetingDate" = null
            }
        } return hasNull;
    }


    /**
     *
     * @param meetingsList
     * @return
     */
    private static Meeting getUnassignedMeeting (List<Meeting> meetingsList) {

        for (Meeting currMeeting : meetingsList) {
            if (currMeeting.meetingDate == null) {
                return currMeeting;
            }
        } return null;
    }


    /**
     *
     * @param meetingAssignmentList
     * @return
     */
    private static List<LocalDate> getFinalAssignment (List<Meeting> meetingAssignmentList) {

        List<LocalDate> output = new ArrayList<>();
        for (Meeting curr : meetingAssignmentList) {
            output.add(curr.meetingDate);
        }
        return output;
    }


    /**
     *
     * @param meetingAssignmentList
     * @param constraints
     * @return
     */
    private static List<Meeting> RecursiveBacktracking (List<Meeting> meetingAssignmentList, Set<DateConstraint> constraints) {

        if ( !(checkForNull(meetingAssignmentList)) ) {
            if (checkMeetingAssignments(meetingAssignmentList, constraints)) {
                return meetingAssignmentList;
            }
        }
        Meeting CurrMeetingAssignment = getUnassignedMeeting(meetingAssignmentList); // Get unassigned variable
        for (LocalDate currDate : CurrMeetingAssignment.domain) {
            CurrMeetingAssignment.meetingDate = currDate; // let curr meeting be assigned a temp date...
            if (checkDomainValue(meetingAssignmentList, constraints, CurrMeetingAssignment)) { // Check Current Meeting's assignment
                List<Meeting> result = RecursiveBacktracking( meetingAssignmentList, constraints);
                if (result != null) {
                    return result;
                }
                CurrMeetingAssignment.meetingDate = null;
            }
        }
        CurrMeetingAssignment.meetingDate = null;
        return null;
    }


    /**
     *
     * @param meetingAssignmentsList
     * @param constraints
     * @return
     */
    private static Boolean checkMeetingAssignments(List<Meeting> meetingAssignmentsList, Set<DateConstraint> constraints) {

        boolean satisfied = false;
        for (DateConstraint currConstraint: constraints) {

            LocalDate leftDate = meetingAssignmentsList.get(currConstraint.L_VAL).meetingDate ;
            LocalDate rightDate = (currConstraint.arity() == 1)
                    ? ( ( (UnaryDateConstraint) currConstraint).R_VAL )
                    : meetingAssignmentsList.get( ((BinaryDateConstraint) currConstraint).R_VAL).meetingDate ;

            switch (currConstraint.OP) {
                case "==":
                    if (leftDate.isEqual(rightDate)) {
                        satisfied = true;
                        break;
                    } else {
                        satisfied = false;
                        return satisfied;
                    }
                case "!=":
                    if (!leftDate.isEqual(rightDate)) {
                        satisfied = true;
                        break;
                    } else {
                        satisfied = false;
                        return satisfied;
                    }
                case ">":
                    if (leftDate.isAfter(rightDate)) {
                        satisfied = true;
                        break;
                    } else {
                        satisfied = false;
                        return satisfied;
                    }
                case "<":
                    if (leftDate.isBefore(rightDate)) {
                        satisfied = true;
                        break;
                    } else {
                        satisfied = false;
                        return satisfied;
                    }
                case ">=":
                    if (leftDate.isAfter(rightDate) || leftDate.isEqual(rightDate)) {
                        satisfied = true;
                        break;
                    } else {
                        satisfied = false;
                        return satisfied;
                    }
                case "<=":
                    if (leftDate.isBefore(rightDate) || leftDate.isEqual(rightDate)) {
                        satisfied = true;
                        break;
                    } else {
                        satisfied = false;
                        return satisfied;
                    }
            }
        }
        return satisfied;
    }


    /**
     *
     * @param meetingAssignmentList
     * @param constraints
     * @return
     */
    private static List<LocalDate> BacktrackingSearch (List<Meeting> meetingAssignmentList, Set<DateConstraint> constraints) {

        List<Meeting> FinalAssignment = RecursiveBacktracking(meetingAssignmentList, constraints);
        List<LocalDate> output;
        if (FinalAssignment == null) {
            output = null;
        } else {
            output = getFinalAssignment(FinalAssignment);
        }
        return output;
    }


    /**
     *
     * @param meetingAssignmentsList
     * @param constraints
     * @param inputMeeting
     * @return
     */
    private static Boolean checkDomainValue(List<Meeting> meetingAssignmentsList, Set<DateConstraint> constraints, Meeting inputMeeting) {

        boolean satisfied = false;
        for (DateConstraint currConstraint: constraints) {
            LocalDate leftDate = null;
            LocalDate rightDate = null;

            if ( currConstraint.arity() == 2) {
                if (inputMeeting.meetingIndex == currConstraint.L_VAL) {
                    leftDate = inputMeeting.meetingDate;
                    rightDate = (currConstraint.arity() == 1)
                            ? ( (UnaryDateConstraint) currConstraint).R_VAL
                            : meetingAssignmentsList.get( ((BinaryDateConstraint) currConstraint).R_VAL).meetingDate;
                } else if (inputMeeting.meetingIndex == ((BinaryDateConstraint) currConstraint).R_VAL) {    // ERROR
                    rightDate = inputMeeting.meetingDate;
                    leftDate = meetingAssignmentsList.get(currConstraint.L_VAL).meetingDate;
                }
            } else if ( currConstraint.arity() == 1 ) {
                leftDate = inputMeeting.meetingDate;
                rightDate = ( (UnaryDateConstraint) currConstraint).R_VAL;
            }

            boolean skipConstraint = false;
            if (rightDate == null || leftDate == null) {  // If a meeting date isn't assigned, skip this constraint
                skipConstraint = true;
                satisfied = true;
            }

            if (!skipConstraint) {
                switch (currConstraint.OP) {
                    case "==":
                        if (leftDate.isEqual(rightDate)) {
                            satisfied = true;
                            break;
                        } else {
                            satisfied = false;
                            return satisfied;
                        }
                    case "!=":
                        if (!leftDate.isEqual(rightDate)) {
                            satisfied = true;
                            break;
                        } else {
                            satisfied = false;
                            return satisfied;
                        }
                    case ">":
                        if (leftDate.isAfter(rightDate)) {
                            satisfied = true;
                            break;
                        } else {
                            satisfied = false;
                            return satisfied;
                        }
                    case "<":
                        if (leftDate.isBefore(rightDate)) {
                            satisfied = true;
                            break;
                        } else {
                            satisfied = false;
                            return satisfied;
                        }
                    case ">=":
                        if (leftDate.isAfter(rightDate) || leftDate.isEqual(rightDate)) {
                            satisfied = true;
                            break;
                        } else {
                            satisfied = false;
                            return satisfied;
                        }
                    case "<=":
                        if (leftDate.isBefore(rightDate) || leftDate.isEqual(rightDate)) {
                            satisfied = true;
                            break;
                        } else {
                            satisfied = false;
                            return satisfied;
                        }
                }
            }
        }
        return satisfied;
    }


    /**
     *
     * @param meetingAssignmentsList
     * @param constraints
     * @return
     */
    private static List<Meeting> NodeConsistency (List<Meeting> meetingAssignmentsList, Set<DateConstraint> constraints) {

        for (Meeting currMeeting : meetingAssignmentsList) {
            List<LocalDate> domainsToRemove = new ArrayList<>();

            for (LocalDate currDate : currMeeting.domain) {
                for (DateConstraint currConstraint: constraints) {

                    LocalDate leftDate = currDate;
                    LocalDate rightDate = null;

                    boolean skipConstraint = false;
                    if ( currConstraint.arity() == 1 ) {
                        rightDate = ( (UnaryDateConstraint) currConstraint).R_VAL;
                    } else {
                        skipConstraint = true;
                    }

                    if (!skipConstraint) {
                        switch (currConstraint.OP) {
                            case "==":
                                if (leftDate.isEqual(rightDate)) {
                                    break;
                                } else {
                                    domainsToRemove.add(leftDate);
                                }
                            case "!=":
                                if (!leftDate.isEqual(rightDate)) {
                                    break;
                                } else {
                                    domainsToRemove.add(leftDate);
                                }
                            case ">":
                                if (leftDate.isAfter(rightDate)) {
                                    break;
                                } else {
                                    domainsToRemove.add(leftDate);
                                }
                            case "<":
                                if (leftDate.isBefore(rightDate)) {
                                    break;
                                } else {
                                    domainsToRemove.add(leftDate);
                                }
                            case ">=":
                                if (leftDate.isAfter(rightDate) || leftDate.isEqual(rightDate)) {
                                    break;
                                } else {
                                    domainsToRemove.add(leftDate);
                                }
                            case "<=":
                                if (leftDate.isBefore(rightDate) || leftDate.isEqual(rightDate)) {
                                    break;
                                } else {
                                    domainsToRemove.add(leftDate);
                                }
                        }
                    }
                }
            }
            currMeeting.domain.removeAll(domainsToRemove);
            if (currMeeting.domain.size() == 0) {
                return null; // If any domain is reduced to zero, there is no possible solution, so return null!
            }
        }
        return meetingAssignmentsList;
    }


    /**
     *
     * @param meetingAssignmentList
     * @param constraints
     * @return
     */
    private static List<Meeting> ArcConsistency (List<Meeting> meetingAssignmentList, Set<DateConstraint> constraints) {

        List<DateConstraint> arcList = getArcs(constraints);
        while ( !(arcList.size() == 0) ) {
            DateConstraint currConstraint = arcList.remove(0);
            Meeting tailMeeting = meetingAssignmentList.get( currConstraint.L_VAL );
            Meeting headMeeting = meetingAssignmentList.get( ((BinaryDateConstraint) currConstraint).R_VAL );

            List<LocalDate> datesRemoved = removeInconsistentValues(tailMeeting, headMeeting, currConstraint, meetingAssignmentList);
            if ( datesRemoved.size() > 0 ) {
                for (LocalDate dateToRemove : datesRemoved) {
                    tailMeeting.domain.remove(dateToRemove);
                }
                if (tailMeeting.domain.size() == 0) {
                    return null;
                }
                List<DateConstraint> arcsToReAdd = new ArrayList<>();
                for ( DateConstraint neighborConstraint : arcList ) {
                    if ( tailMeeting.meetingIndex ==  ((BinaryDateConstraint) neighborConstraint).R_VAL) {
                        if ( !(arcsToReAdd.contains(neighborConstraint)) && !(arcList.contains(neighborConstraint)) ) {
                            arcsToReAdd.add(neighborConstraint);
                        }
                    }
                }
                arcList.addAll(arcsToReAdd);
            }
        }
        return meetingAssignmentList;
    }


    /**
     *
     * @param tailMeeting
     * @param headMeeting
     * @param currConstraint
     * @param meetingAssignmentList
     * @return
     */
    private static List<LocalDate> removeInconsistentValues (Meeting tailMeeting, Meeting headMeeting, DateConstraint currConstraint, List<Meeting> meetingAssignmentList) {

        List<LocalDate> datesToRemove = new ArrayList<>();
        for (LocalDate tail : tailMeeting.domain) {
            if ( !(checkArcDomains(tail, headMeeting, currConstraint)) ) {
                if ( !(datesToRemove.contains(tail)) ) {
                    datesToRemove.add(tail);
                }
            }
        }
        return datesToRemove;
    }


    /**
     *
     * @param tail
     * @param headMeeting
     * @param currConstraint
     * @return
     */
    private static boolean checkArcDomains (LocalDate tail, Meeting headMeeting, DateConstraint currConstraint) {

        boolean satisfied = false;
        for (LocalDate head : headMeeting.domain) {
            switch (currConstraint.OP) {
                case "==":
                    if (tail.isEqual(head)) {
                        satisfied = true;
                        return satisfied;
                    } else {
                        break;
                    }
                case "!=":
                    if (!tail.isEqual(head)) {
                        satisfied = true;
                        return satisfied;
                    } else {
                        break;
                    }
                case ">":
                    if (tail.isAfter(head)) {
                        satisfied = true;
                        return satisfied;
                    } else {
                        break;
                    }
                case "<":
                    if (tail.isBefore(head)) {
                        satisfied = true;
                        return satisfied;
                    } else {
                        break;
                    }
                case ">=":
                    if (tail.isAfter(head) || tail.isEqual(head)) {
                        satisfied = true;
                        return satisfied;
                    } else {
                        break;
                    }
                case "<=":
                    if (tail.isBefore(head) || tail.isEqual(head)) {
                        satisfied = true;
                        return satisfied;
                    } else {
                        break;
                    }
            }
        }
        return satisfied;
    }


    /**
     *
     * @param constraints
     * @return
     */
    private static List<DateConstraint> getArcs (Set<DateConstraint> constraints) {

        HashMap<String, String> reciprocalOPS = new HashMap<>();
        reciprocalOPS.put("==", "==");
        reciprocalOPS.put("!=", "!=");
        reciprocalOPS.put("<", ">");
        reciprocalOPS.put("<=", "=>");
        reciprocalOPS.put(">", "<");
        reciprocalOPS.put(">=", "<=");

        List<DateConstraint> Arcs = new ArrayList<>(); // Set of ALL constraints (originals + their reciprocals)
        for (DateConstraint currConstraint : constraints) {
            if ( currConstraint.arity() == 2 ) {
                int newL_Val = ((BinaryDateConstraint) currConstraint).R_VAL;
                int newR_Val = currConstraint.L_VAL;
                String newOP = reciprocalOPS.get(currConstraint.OP);

                DateConstraint reciprocalConstraint = new BinaryDateConstraint(newL_Val, newOP, newR_Val);
                Arcs.add(currConstraint);
                Arcs.add(reciprocalConstraint);
            }
        }
        return Arcs;
    }


    /**
     * The following object is used to create new Meeting objects that will store a meeting's date,
     * index in the assignment list, domain of possible meeting dates, and the constraints.
     */
    private static class Meeting {

        int meetingIndex;
        LocalDate rangeStart;
        LocalDate rangeEnd;
        List<LocalDate> domain;
        Set<DateConstraint> constraints;
        LocalDate meetingDate = null;

        Meeting(int meetingIndex, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) {
            this.meetingIndex = meetingIndex;
            this.rangeStart = rangeStart;
            this.rangeEnd = rangeEnd;
            this.domain = getDomain(rangeStart, rangeEnd);
            this.constraints = constraints;
        }

        private static List<LocalDate> getDomain(LocalDate rangeStart, LocalDate rangeEnd) {
            List<LocalDate> domain = new ArrayList<>();
            while ( !(rangeStart.equals(rangeEnd.plusDays(1))) ) {
                domain.add(rangeStart);
                rangeStart = rangeStart.plusDays(1);
            }
            return domain;
        }
    }

}