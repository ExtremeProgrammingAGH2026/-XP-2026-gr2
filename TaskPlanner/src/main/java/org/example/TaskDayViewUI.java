package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class TaskDayViewUI {

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final TaskReadService taskReadService;
    private final TaskPrintService taskPrintService;
    private final TaskScheduleService taskScheduleService;
    private final String tasksFilePath;

    public TaskDayViewUI(TaskReadService taskReadService, TaskPrintService taskPrintService,
                         TaskScheduleService taskScheduleService, String tasksFilePath) {
        this.taskReadService = taskReadService;
        this.taskPrintService = taskPrintService;
        this.taskScheduleService = taskScheduleService;
        this.tasksFilePath = tasksFilePath;
    }

    public void show(Scanner scanner) {
        LocalDate date = promptDate(scanner);
        if (date == null) {
            return;
        }

        List<Task> tasks = taskReadService.readTasks(tasksFilePath);
        List<Task> dayTasks = taskScheduleService.getTasksForDay(tasks, date);
        taskPrintService.printTasks(dayTasks);
    }

    private LocalDate promptDate(Scanner scanner) {
        while (true) {
            System.out.print("Date (dd.MM.yyyy): ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, DAY_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Use dd.MM.yyyy");
            }
        }
    }
}