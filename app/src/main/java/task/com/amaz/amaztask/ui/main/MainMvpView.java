package task.com.amaz.amaztask.ui.main;

import java.util.List;

import task.com.amaz.amaztask.data.network.model.Entry;
import task.com.amaz.amaztask.ui.base.MvpView;

public interface MainMvpView extends MvpView {
    void setEntries(List<Entry> entries);

    void showError();
}
