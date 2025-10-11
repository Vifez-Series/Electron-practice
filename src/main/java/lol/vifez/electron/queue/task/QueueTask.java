package lol.vifez.electron.queue.task;

import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.queue.Queue;
import lol.vifez.electron.queue.QueueManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@RequiredArgsConstructor
public class QueueTask extends BukkitRunnable {

    private final QueueManager queueManager;

    @Override
    public void run() {
        for (Kit kit : Practice.getInstance().getKitManager().getKits().values()) {
            if (queueManager.getQueue(kit, false) == null) {
                queueManager.getQueueMap().put(kit.getName(), new Queue(Practice.getInstance(), kit, false));

                if (kit.isRanked()) {
                    queueManager.getQueueMap().put("ranked_" + kit.getName(), new Queue(Practice.getInstance(), kit, true));
                }
            }
        }

        queueManager.getQueueMap().values().forEach(Queue::move);
    }
}
