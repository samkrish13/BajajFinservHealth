@Service
public class ApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void processAndSend() {
        String genWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook";

        Map<String, Object> requestBody = Map.of(
            "name", "John Doe",
            "regNo", "REG12347",
            "email", "john@example.com"
        );

        try {
            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                genWebhookUrl, requestBody, WebhookResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                WebhookResponse body = response.getBody();
                Map<String, Object> result = solveProblem(body.getData(), body.getRegNo());
                sendWithRetry(body.getWebhook(), body.getAccessToken(), result, 4);
            }
        } catch (Exception e) {
            System.err.println("Failed to call /generateWebhook: " + e.getMessage());
        }
    }

    private Map<String, Object> solveProblem(Map<String, Object> data, String regNo) {
        int lastDigit = Character.getNumericValue(regNo.charAt(regNo.length() - 1));
        boolean isOdd = lastDigit % 2 == 1;
        List<Map<String, Object>> users = (List<Map<String, Object>>) data.get("users");

        if (isOdd) {
            // Question 1: Mutual Followers
            return Map.of(
                "regNo", regNo,
                "outcome", getMutualFollowers(users)
            );
        } else {
            // Question 2: Nth Level Follows
            int n = (int) data.get("n");
            int findId = (int) data.get("findId");
            return Map.of(
                "regNo", regNo,
                "outcome", getNthLevelFollows(users, findId, n)
            );
        }
    }

    private List<List<Integer>> getMutualFollowers(List<Map<String, Object>> users) {
        Map<Integer, Set<Integer>> followMap = new HashMap<>();
        for (Map<String, Object> user : users) {
            int id = (int) user.get("id");
            List<Integer> follows = (List<Integer>) user.get("follows");
            followMap.put(id, new HashSet<>(follows));
        }

        Set<List<Integer>> mutualPairs = new HashSet<>();
        for (int id : followMap.keySet()) {
            for (int followId : followMap.get(id)) {
                if (followMap.containsKey(followId) && followMap.get(followId).contains(id)) {
                    List<Integer> pair = Arrays.asList(Math.min(id, followId), Math.max(id, followId));
                    mutualPairs.add(pair);
                }
            }
        }

        return new ArrayList<>(mutualPairs);
    }

    private List<Integer> getNthLevelFollows(List<Map<String, Object>> users, int startId, int level) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (Map<String, Object> user : users) {
            int id = (int) user.get("id");
            List<Integer> follows = (List<Integer>) user.get("follows");
            graph.put(id, follows);
        }

        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(startId);
        visited.add(startId);

        for (int i = 0; i < level; i++) {
            int size = queue.size();
            Set<Integer> nextLevel = new HashSet<>();
            for (int j = 0; j < size; j++) {
                int current = queue.poll();
                List<Integer> neighbors = graph.getOrDefault(current, new ArrayList<>());
                for (int neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        nextLevel.add(neighbor);
                    }
                }
            }
            queue.addAll(nextLevel);
            visited.addAll(nextLevel);
        }

        return new ArrayList<>(queue);
    }

    private void sendWithRetry(String url, String token, Map<String, Object> payload, int attempts) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        for (int i = 1; i <= attempts; i++) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Webhook successfully triggered on attempt " + i);
                    break;
                } else {
                    System.err.println("Attempt " + i + " failed. Retrying...");
                }
            } catch (Exception e) {
                System.err.println("Attempt " + i + " failed: " + e.getMessage());
            }
        }
    }
}
