@Component
public class ApiRunner implements CommandLineRunner {

    private final ApiService apiService;

    public ApiRunner(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void run(String... args) {
        apiService.processAndSend();
    }
}
