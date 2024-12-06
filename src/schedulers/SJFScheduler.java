package schedulers;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import models.Process;

public class SJFScheduler implements Scheduler {

    public int contextSwitchingTime;
    public List<Process> executedProcesses = new ArrayList<>();
    public int totalWaitingTime = 0;
    public int totalTurnaroundTime = 0;
    public int totalProcesses;

    public SJFScheduler(int contextSwitchingTime) {
        this.contextSwitchingTime = contextSwitchingTime;
    }

    @Override
    public void schedule(List<Process> processes) {
        // ترتيب العمليات الاول حسب  (Arrival Time)
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        totalProcesses = processes.size(); // عدد العمليات الإجمالي
        List<Process> readyQueue = new ArrayList<>();
        int currentTime = 0; // الوقت الحالي
        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            while (!processes.isEmpty() && processes.get(0).getArrivalTime() <= currentTime) {
                readyQueue.add(processes.remove(0));
            }

            if (!readyQueue.isEmpty()) {
                // Aging
                for (Process p : readyQueue) {
                    int waitingTime = currentTime - p.getArrivalTime();
                    // زيادة بريوريتي البروسيس لو بتستنى لفترة طويلة
                    if (waitingTime > 10) {
                        p.setPriority(p.getPriority() - 1); // زيادة البريوريتي
                    }
                }


                // ترتيب العمليات في الطابور الجاهز حسب  (Burst Time) و Arrival Time
                readyQueue.sort(Comparator.comparingInt(Process::getBurstTime)
                        .thenComparingInt(Process::getArrivalTime));

                // أخذ العملية التي تحتاج أقل وقت تنفيذ
                Process currentProcess = readyQueue.remove(0);
                int burstTime = currentProcess.getBurstTime();

                // حساب زمن الانتظار و زمن الانتهاء
                currentProcess.setWaitingTime(currentTime - currentProcess.getArrivalTime());
                currentProcess.setTurnaroundTime(currentProcess.getWaitingTime() + burstTime);

                 totalWaitingTime += currentProcess.getWaitingTime();
                totalTurnaroundTime += currentProcess.getTurnaroundTime();

                 currentTime += burstTime;

                executedProcesses.add(currentProcess);
            } else {
                // لو الطابور فاضي نستنى تيجي بروسيس جديدة
                currentTime++;
            }
        }
    }

    @Override
    public void printMetrics() {
          System.out.println("Average Waiting Time: " + (double) totalWaitingTime / totalProcesses);
        System.out.println("Average Turnaround Time: " + (double) totalTurnaroundTime / totalProcesses);
    }

    @Override
    public void printExecutionOrder() {
         for (Process p : executedProcesses) {
            System.out.println("Process " + p.getName() + " executed at time " + p.getTurnaroundTime()
                    + " (Waiting Time: " + p.getWaitingTime()
                    + ", Turnaround Time: " + p.getTurnaroundTime() + ")");
        }
    }
}
