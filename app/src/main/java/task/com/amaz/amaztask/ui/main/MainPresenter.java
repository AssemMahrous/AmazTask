package task.com.amaz.amaztask.ui.main;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import task.com.amaz.amaztask.data.AppDataManager;
import task.com.amaz.amaztask.data.network.model.Entry;
import task.com.amaz.amaztask.data.network.model.MessageResponse;
import task.com.amaz.amaztask.ui.base.BasePresenter;

public class MainPresenter<V extends MainMvpView> extends BasePresenter<V> implements MainMvpPresenter<V> {

    MainPresenter(AppDataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void getData() {
        getDataManager()
                .getApi()
                .getMessages()
                .enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                setData(response.body().getFeed().getEntry());
                            } else {
                                showError();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showError();
                        }
                    }


                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        showError();
                    }
                });
    }

    @Override
    public void setData(List<Entry> entries) {
        getMvpView().setEntries(entries);
    }

    private void showError() {
        getMvpView().showError();
    }
}
