package br.com.api.todolist.controller;

import br.com.api.todolist.dto.tasks.CreateTaskDTO;
import br.com.api.todolist.dto.tasks.ListTaskDTO;
import br.com.api.todolist.dto.tasks.UpdateTaskDTO;
import br.com.api.todolist.dto.tasks.ViewTaskDTO;
import br.com.api.todolist.entity.Task;
import br.com.api.todolist.repository.TaskRepository;
import br.com.api.todolist.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskService service;

    @PostMapping
    @Transactional
    public ResponseEntity create(@RequestBody CreateTaskDTO dados, UriComponentsBuilder uriBuilder) {
        var task = new Task(dados);
        service.validate(task);
        repository.save(task);

        var uri = uriBuilder.path("/tasks/{id}")
                .buildAndExpand(task.getId()).toUri();

        return ResponseEntity.created(uri).body(new ViewTaskDTO(task));
    }

    @GetMapping
    public ResponseEntity<Page<ListTaskDTO>> list(@PageableDefault(size = 10, sort = {"startAt"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ListTaskDTO> tasks = repository.findAll(pageable)
                .map(ListTaskDTO::new);

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity view(@PathVariable UUID id) {
        var task = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Este ID não existe"));
        return ResponseEntity.ok(new ListTaskDTO(task));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity update(@PathVariable UUID id, @RequestBody UpdateTaskDTO update) {
        var task = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));
        task.updateTask(update);

        var taskUpdated = repository.save(task);
        return ResponseEntity.ok().body(new ViewTaskDTO(taskUpdated));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity delete(@PathVariable UUID id) {
        var task = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));
        service.delete(task);

        return ResponseEntity.noContent().build();
    }
}
