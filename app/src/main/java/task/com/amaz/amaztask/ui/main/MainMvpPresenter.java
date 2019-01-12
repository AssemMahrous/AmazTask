package task.com.amaz.amaztask.ui.main;

import java.util.List;

import task.com.amaz.amaztask.data.network.model.Entry;
import task.com.amaz.amaztask.ui.base.MvpPresenter;

public interface MainMvpPresenter<V extends MainMvpView> extends MvpPresenter<V> {

    void getData();

    void setData(List<Entry> entries);
}
