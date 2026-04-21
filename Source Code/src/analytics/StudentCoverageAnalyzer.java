package analytics;

import entityClasses.Post;
import entityClasses.Reply;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * Computes whether each student answered questions from the required number of unique peers.
 */
public class StudentCoverageAnalyzer {

    private static final int MINIMUM_THRESHOLD = 1;

    /**
     * Processes a discussion export and produces statistics about how students answered peer questions.
     *
     * @param questions             list of questions/posts captured from the board
     * @param replies               replies associated with those posts
     * @param requiredUniquePeers   minimum unique question authors each student must assist
     * @return immutable coverage result used by graders and dashboards
     */
    public StudentCoverageResult analyze(List<Post> questions, List<Reply> replies, int requiredUniquePeers) {
        if (requiredUniquePeers < MINIMUM_THRESHOLD) {
            throw new IllegalArgumentException("Threshold must be at least " + MINIMUM_THRESHOLD);
        }

        Map<String, Post> questionsById = new LinkedHashMap<>();
        Map<String, String> questionOwnerById = new LinkedHashMap<>();
        for (Post question : safeList(questions)) {
            if (question == null || question.getID() == null) {
                continue;
            }
            questionsById.put(question.getID(), question);
            questionOwnerById.put(question.getID(), normalize(question.getUser()));
        }

        Map<String, Integer> replyCountsByQuestion = new HashMap<>();
        Map<String, StudentAccumulator> accumulators = new TreeMap<>();
        List<String> orphanReplyIds = new ArrayList<>();

        for (Reply reply : safeList(replies)) {
            if (reply == null) {
                continue;
            }
            String questionId = reply.getPostID();
            String replyId = reply.getID();
            if (questionId == null || !questionOwnerById.containsKey(questionId)) {
                // Record orphan replies so graders can fix bad exports instead of silently skipping data.
                if (replyId != null) {
                    orphanReplyIds.add(replyId);
                }
                continue;
            }

            replyCountsByQuestion.merge(questionId, 1, Integer::sum);

            String respondingStudent = normalize(reply.getUser());
            if (respondingStudent == null) {
                continue;
            }

            String questionOwner = questionOwnerById.get(questionId);
            StudentAccumulator accumulator = accumulators.computeIfAbsent(respondingStudent, StudentAccumulator::new);
            accumulator.recordReply(questionId, questionOwner);
        }

        List<String> unansweredQuestionIds = new ArrayList<>();
        for (String questionId : questionOwnerById.keySet()) {
            if (!replyCountsByQuestion.containsKey(questionId)) {
                unansweredQuestionIds.add(questionId);
            }
        }
        unansweredQuestionIds.sort(Comparator.naturalOrder());
        Collections.sort(orphanReplyIds);

        Map<String, StudentCoverageSummary> summaries = new LinkedHashMap<>();
        Set<String> studentsMeeting = new LinkedHashSet<>();
        Set<String> studentsBelow = new LinkedHashSet<>();

        for (Map.Entry<String, StudentAccumulator> entry : accumulators.entrySet()) {
            StudentCoverageSummary summary = entry.getValue().toSummary();
            summaries.put(entry.getKey(), summary);
            if (summary.meetsThreshold(requiredUniquePeers)) {
                studentsMeeting.add(entry.getKey());
            } else {
                studentsBelow.add(entry.getKey());
            }
        }

        return new StudentCoverageResult(requiredUniquePeers, summaries, unansweredQuestionIds,
                orphanReplyIds, studentsMeeting, studentsBelow);
    }

    private static <T> List<T> safeList(List<T> data) {
        return data == null ? Collections.emptyList() : data;
    }

    private static String normalize(String username) {
        if (username == null) {
            return null;
        }
        String trimmed = username.trim();
        return trimmed.isEmpty() ? null : trimmed.toLowerCase(Locale.US);
    }

    /**
     * Builder used internally to aggregate replies before producing immutable summaries.
     */
    private static final class StudentAccumulator {
        private final String studentId;
        private final Map<String, Integer> questionReplyCounts = new LinkedHashMap<>();
        private final Map<String, Integer> peerTouchCounts = new LinkedHashMap<>();

        StudentAccumulator(String studentId) {
            this.studentId = Objects.requireNonNull(studentId);
        }

        void recordReply(String questionId, String questionOwner) {
            questionReplyCounts.merge(questionId, 1, Integer::sum);
            if (!studentId.equals(questionOwner)) {
                peerTouchCounts.merge(questionOwner, 1, Integer::sum);
            }
        }

        StudentCoverageSummary toSummary() {
            return new StudentCoverageSummary(studentId, questionReplyCounts, peerTouchCounts);
        }
    }
}