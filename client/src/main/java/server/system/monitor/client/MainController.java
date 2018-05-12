package server.system.monitor.client;

import com.google.gson.Gson;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Swap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.system.monitor.listener.OnSocketEventListener;
import server.system.monitor.model.SystemStatus;
import server.system.monitor.util.Utils;


public class MainController implements Initializable, OnSocketEventListener {

  private static final String MEM_LABEL_FORMAT = "%s / %s (%.0f%%)";
  private static final String EMPTY_MEM_LABEL_TEXT = "0 GB / 0 GB (0%)";
  private static final String EMPTY_CPU_LABEL_FORMAT = "0%";
  private static final int MASTER_ID = 0;
  private static final int SLAVE1_ID = 1;
  private static final int SLAVE2_ID = 2;
  private final Logger logger = LoggerFactory.getLogger(MainController.class);
  @FXML
  private TitledPane masterPane;
  @FXML
  private Label masterCpuLabel;
  @FXML
  private ProgressBar masterCpuProgress;
  @FXML
  private Label masterSysMemLabel;
  @FXML
  private ProgressBar masterSysMemProgress;
  @FXML
  private Label masterSwapMemLabel;
  @FXML
  private ProgressBar masterSwapMemProgress;
  @FXML
  private ToggleButton masterToggleBtn;

  @FXML
  private TitledPane slave1Pane;
  @FXML
  private Label slave1CpuLabel;
  @FXML
  private ProgressBar slave1CpuProgress;
  @FXML
  private Label slave1SysMemLabel;
  @FXML
  private ProgressBar slave1SysMemProgress;
  @FXML
  private Label slave1SwapMemLabel;
  @FXML
  private ProgressBar slave1SwapMemProgress;
  @FXML
  private ToggleButton slave1ToggleBtn;

  @FXML
  private TitledPane slave2Pane;
  @FXML
  private Label slave2CpuLabel;
  @FXML
  private ProgressBar slave2CpuProgress;
  @FXML
  private Label slave2SysMemLabel;
  @FXML
  private ProgressBar slave2SysMemProgress;
  @FXML
  private Label slave2SwapMemLabel;
  @FXML
  private ProgressBar slave2SwapMemProgress;
  @FXML
  private ToggleButton slave2ToggleBtn;

  private List<List<Label>> bindingLabels = new ArrayList<>();
  private List<List<ProgressBar>> bindingProgressBars = new ArrayList<>();
  private List<ToggleButton> bindingToggleButtons = new ArrayList<>();

  private ClientThread runningThreads[] = new ClientThread[3];

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    List<Label> masterLabels = new ArrayList<>();
    masterLabels.add(masterCpuLabel);
    masterLabels.add(masterSysMemLabel);
    masterLabels.add(masterSwapMemLabel);
    bindingLabels.add(masterLabels);

    List<ProgressBar> masterProgressBars = new ArrayList<>();
    masterProgressBars.add(masterCpuProgress);
    masterProgressBars.add(masterSysMemProgress);
    masterProgressBars.add(masterSwapMemProgress);
    bindingProgressBars.add(masterProgressBars);

    List<Label> slave1Labels = new ArrayList<>();
    slave1Labels.add(slave1CpuLabel);
    slave1Labels.add(slave1SysMemLabel);
    slave1Labels.add(slave1SwapMemLabel);
    bindingLabels.add(slave1Labels);

    List<ProgressBar> slave1ProgressBars = new ArrayList<>();
    slave1ProgressBars.add(slave1CpuProgress);
    slave1ProgressBars.add(slave1SysMemProgress);
    slave1ProgressBars.add(slave1SwapMemProgress);
    bindingProgressBars.add(slave1ProgressBars);

    List<Label> slave2Labels = new ArrayList<>();
    slave2Labels.add(slave2CpuLabel);
    slave2Labels.add(slave2SysMemLabel);
    slave2Labels.add(slave2SwapMemLabel);
    bindingLabels.add(slave2Labels);

    List<ProgressBar> slave2ProgressBars = new ArrayList<>();
    slave2ProgressBars.add(slave2CpuProgress);
    slave2ProgressBars.add(slave2SysMemProgress);
    slave2ProgressBars.add(slave2SwapMemProgress);
    bindingProgressBars.add(slave2ProgressBars);

    bindingToggleButtons.add(masterToggleBtn);
    bindingToggleButtons.add(slave1ToggleBtn);
    bindingToggleButtons.add(slave2ToggleBtn);

    masterToggleBtn.setOnAction(event -> {
      if (masterToggleBtn.isSelected()) {
        ClientThread clientThread =
            new ClientThread(this, MASTER_ID,
                ServerConstants.ADDRESS[0], ServerConstants.PORTS[0]);
        runningThreads[0] = clientThread;
        runningThreads[0].start();
        masterToggleBtn.setDisable(true);
      } else {
        runningThreads[0].interrupt();
      }
    });

    slave1ToggleBtn.setOnAction(event -> {
      if (slave1ToggleBtn.isSelected()) {
        ClientThread clientThread =
            new ClientThread(this, SLAVE1_ID,
                ServerConstants.ADDRESS[1], ServerConstants.PORTS[1]);
        runningThreads[1] = clientThread;
        runningThreads[1].start();
        slave1ToggleBtn.setDisable(true);
      } else {
        runningThreads[1].interrupt();
      }
    });

    slave2ToggleBtn.setOnAction(event -> {
      if (slave2ToggleBtn.isSelected()) {
        ClientThread clientThread =
            new ClientThread(this, SLAVE2_ID,
                ServerConstants.ADDRESS[2], ServerConstants.PORTS[2]);
        runningThreads[2] = clientThread;
        runningThreads[2].start();
        slave2ToggleBtn.setDisable(true);
      } else {
        runningThreads[2].interrupt();
      }
    });
  }

  @Override
  public void onStarted(int port, int id) {
    logger.info("Server connection started (port: " + port + ", serverId: " + id + ")");
    Platform.runLater(() -> {
      if (id == MASTER_ID) {
        masterPane.setText("Master [Connected]");
        masterToggleBtn.setDisable(false);
      } else if (id == SLAVE1_ID) {
        slave1Pane.setText("Slave1 [Connected]");
        slave1ToggleBtn.setDisable(false);
      } else if (id == SLAVE2_ID) {
        slave2Pane.setText("Slave2 [Connected]");
        slave2ToggleBtn.setDisable(false);
      }
    });
  }

  @Override
  public void onMessageReceived(int id, String message) {
    logger.info("Received message from " + id);
    Platform.runLater(() -> processMessage(id, message));
  }

  @Override
  public void onMessageSent(int id) {
    // No-op
  }

  @Override
  public void onError(int id, Exception e) {
    logger.error("Error occurred (" + id + ")");
    e.printStackTrace();
    StringBuilder stackTraceContent = new StringBuilder();
    for (StackTraceElement traceElement : e.getStackTrace()) {
      stackTraceContent.append(traceElement.toString()).append("\n");
    }

    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error occurred");
      alert.setHeaderText(e.toString());
      alert.setContentText(stackTraceContent.toString());
      alert.show();
      clearView(id);
    });
  }

  @Override
  public void onClosed(int id) {
    Platform.runLater(() -> clearView(id));
    logger.info("Server connection closed (serverId: " + id + ")");
  }

  @Override
  public void onAccepted(int id) {
    // No-op
  }

  @Override
  public void onDisconnected(int id) {
    // No-op
  }

  public void interruptAllThreads() {
    for (int index = 0; index < 3; index++) {
      ClientThread thread = runningThreads[index];
      if (thread != null && thread.isAlive()) {
        thread.interrupt();
      }
    }
  }

  private void processMessage(int id, String message) {
    SystemStatus systemStatus = parseMessage(message);
    showView(id, systemStatus);
  }

  private void showView(int id, SystemStatus systemStatus) {
    CpuPerc cpu = systemStatus.getCpu();
    Mem mem = systemStatus.getMem();
    Swap swap = systemStatus.getSwap();

    double memPercent = ((double) mem.getActualUsed() / mem.getTotal());
    double swapPercent = ((double) swap.getUsed() / swap.getTotal());

    List<Label> labels = bindingLabels.get(id);
    labels.get(0).setText(CpuPerc.format(cpu.getCombined()));

    labels.get(1).setText(String.format(MEM_LABEL_FORMAT,
        Utils.toMemoryFormat(mem.getActualUsed()),
        Utils.toMemoryFormat(mem.getTotal()),
        memPercent * 100));
    labels.get(2).setText(String.format(MEM_LABEL_FORMAT,
        Utils.toMemoryFormat(swap.getUsed()),
        Utils.toMemoryFormat(swap.getTotal()),
        swapPercent * 100));

    List<ProgressBar> progressBars = bindingProgressBars.get(id);
    progressBars.get(0).setProgress(cpu.getCombined());
    progressBars.get(1).setProgress(memPercent);
    progressBars.get(2).setProgress(swapPercent);
  }

  private void clearView(int id) {
    List<Label> labels = bindingLabels.get(id);
    labels.get(0).setText(EMPTY_CPU_LABEL_FORMAT);
    labels.get(1).setText(EMPTY_MEM_LABEL_TEXT);
    labels.get(2).setText(EMPTY_MEM_LABEL_TEXT);
    List<ProgressBar> progressBars = bindingProgressBars.get(id);
    for (ProgressBar progressBar : progressBars) {
      progressBar.setProgress(0);
    }
    bindingToggleButtons.get(id).setSelected(false);
    bindingToggleButtons.get(id).setDisable(false);

    if (id == MASTER_ID) {
      masterPane.setText("Master [Disconnected]");
    } else if (id == SLAVE1_ID) {
      slave1Pane.setText("Slave1 [Disconnected]");
    } else if (id == SLAVE2_ID) {
      slave2Pane.setText("Slave2 [Disconnected]");
    }
  }

  private SystemStatus parseMessage(String message) {
    Gson gson = new Gson();
    return gson.fromJson(message.trim(), SystemStatus.class);
  }
}
