package task.com.amaz.amaztask.data;

import android.content.Context;

import task.com.amaz.amaztask.data.network.AmazApi;

public class AppDataManager {

    private final AmazApi amazApi;

    public AppDataManager(Context context,
                          AmazApi amazApi) {
        this.amazApi = amazApi;
    }

    public AmazApi getApi() {
        return amazApi;
    }
}
