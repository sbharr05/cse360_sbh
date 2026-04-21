package analytics;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Immutable value object returned by {@link StudentCoverageAnalyzer} summarizing how
 * the class performed relative to the "three unique peers" rule.
 */
public class StudentCoverageResult {

    private final int requiredPeerThreshold;
    private final Map<String, StudentCoverageSummary> summaries;
    private final List<String> unansweredQuestionIds;
    private final List<String> orphanReplyIds;
    private final Set<String> studentsMeetingThreshold;
    private final Set<String> studentsBelowThreshold;

    StudentCoverageResult(int requiredPeerThreshold,
            Map<String, StudentCoverageSummary> summaries,
            List<String> unansweredQuestionIds,
            List<String> orphanReplyIds,
            Set<String> studentsMeetingThreshold,
            Set<String> studentsBelowThreshold) {
        this.requiredPeerThreshold = requiredPeerThreshold;
        this.summaries = Collections.unmodifiableMap(new LinkedHashMap<>(summaries));
        this.unansweredQuestionIds = List.copyOf(unansweredQuestionIds);
        this.orphanReplyIds = List.copyOf(orphanReplyIds);
        this.studentsMeetingThreshold = Collections.unmodifiableSet(new LinkedHashSet<>(studentsMeetingThreshold));
        this.studentsBelowThreshold = Collections.unmodifiableSet(new LinkedHashSet<>(studentsBelowThreshold));
    }

    /**
     * @return threshold configured for this analysis run
     */
    public int getRequiredPeerThreshold() {
        return requiredPeerThreshold;
    }

    /**
     * @return immutable view of all student coverage summaries indexed by username
     */
    public Map<String, StudentCoverageSummary> getSummaries() {
        return summaries;
    }

    /**
     * @return list of post identifiers that never received a reply
     */
    public List<String> getUnansweredQuestionIds() {
        return unansweredQuestionIds;
    }

    /**
     * @return list of reply identifiers whose parent questions were missing from the export
     */
    public List<String> getOrphanReplyIds() {
        return orphanReplyIds;
    }

    /**
     * @return students who already meet or exceed the peer threshold
     */
    public Set<String> getStudentsMeetingThreshold() {
        return studentsMeetingThreshold;
    }

    /**
     * @return students who still need more peer responses
     */
    public Set<String> getStudentsBelowThreshold() {
        return studentsBelowThreshold;
    }

    /**
     * Convenience helper for dashboards that want a quick PASS/FAIL check.
     *
     * @param studentId student identifier to look up
     * @return true when the student both exists in the summary map and meets the threshold
     */
    public boolean isStudentCompliant(String studentId) {
        StudentCoverageSummary summary = summaries.get(studentId);
        return summary != null && summary.meetsThreshold(requiredPeerThreshold);
    }

    /**
     * @param studentId student identifier to look up
     * @return missing peer count for the given student, or the required threshold when no activity is recorded
     */
    public int peersRemaining(String studentId) {
        StudentCoverageSummary summary = summaries.get(studentId);
        if (summary == null) {
            return requiredPeerThreshold;
        }
        return summary.peersRemaining(requiredPeerThreshold);
    }
}