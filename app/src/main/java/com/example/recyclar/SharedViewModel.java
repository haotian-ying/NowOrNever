package com.example.recyclar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<ToDo> newTodo = new MutableLiveData<>();

    public void setNewTodo(ToDo todo) {
        newTodo.setValue(todo);
    }

    public LiveData<ToDo> getNewTodo() {
        return newTodo;
    }
}
