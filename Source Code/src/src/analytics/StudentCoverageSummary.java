package analytics;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Immutable snapshot describing how many unique peers and question threads a student
 * responded to during the coverage analysis.
 */
public class StudentCoverageSummary {

    private final String studentId;
    private final Map<String, Integer> questionReplyCounts;
    private final Map<String, Integer> peerTouchCounts;

    StudentCoverageSummary(String studentId, Map<String, Integer> questionReplyCounts,
            Map<String, Integer> peerTouchCounts) {
        this.studentId = studentId;
        this.questionReplyCounts = Collections.unmodifiableMap(new LinkedHashMap<>(questionReplyCounts));
        this.peerTouchCounts = Collections.unmodifiableMap(new LinkedHashMap<>(peerTouchCounts));
    }

    /**
     * @return the username tied to this summary.
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * @return the number of replies per question ID handled by this student.
     */
    public Map<String, Integer> getQuestionReplyCounts() {
        return questionReplyCounts;
    }

    /**
     * @return the identifiers of the question authors this student helped.
     */
    public Set<String> getPeerQuestioners() {
        return peerTouchCounts.keySet();
    }

    /**
     * @return the total number of distinct peers this student has helped.
     */
    public int getUniquePeerCount() {
        return peerTouchCounts.size();
    }

    /**
     * Evaluates whether the student satisfied the unique-peer requirement.
     *
     * @param requiredUniquePeers minimum peers that must be answered
     * @return true when the peer count meets or exceeds the threshold
     */
    public boolean meetsThreshold(int requiredUniquePeers) {
        return getUniquePeerCount() >= requiredUniquePeers;
    }

    /**
     * @param requiredUniquePeers minimum peers expected
     * @return the remaining peer count needed to meet the threshold (zero when already satisfied)
     */
    public int peersRemaining(int requiredUniquePeers) {
        int remaining = requiredUniquePeers - getUniquePeerCount();
        return Math.max(0, remaining);
    }

    /**
     * @return how many responses each peer questioner received from this student
     */
    public Map<String, Integer> getPeerTouchCounts() {
        return peerTouchCounts;
    }
}