package task.com.amaz.amaztask.data.network;

import retrofit2.Call;
import retrofit2.http.GET;
import task.com.amaz.amaztask.data.network.model.MessageResponse;

/**
 * Created by assem on 2/8/2018.
 */

public interface AmazApi {

    @GET("0Ai2EnLApq68edEVRNU0xdW9QX1BqQXhHRl9sWDNfQXc/od6/public/basic?alt=json")
    Call<MessageResponse> getMessages();
}
