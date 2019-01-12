package task.com.amaz.amaztask;

import android.app.Application;

import task.com.amaz.amaztask.data.AppDataManager;
import task.com.amaz.amaztask.data.network.ApiModule;

public class MvpApp extends Application {
    AppDataManager appDataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        ApiModule apiModule = new ApiModule();
        appDataManager = new AppDataManager(getApplicationContext(),
                apiModule.provideApiService());
    }

    public AppDataManager getDataManager() {
        return appDataManager;
    }
}
