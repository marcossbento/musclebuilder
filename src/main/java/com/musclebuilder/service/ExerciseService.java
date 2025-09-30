package com.musclebuilder.service;

import com.musclebuilder.dto.ExerciseDTO;
import com.musclebuilder.exception.ResourceNotFoundException;
import com.musclebuilder.mapper.ExerciseMapper;
import com.musclebuilder.model.Exercise;
import com.musclebuilder.model.MuscleGroup;
import com.musclebuilder.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;

    @Autowired
    public ExerciseService(ExerciseRepository exerciseRepository, ExerciseMapper exerciseMapper
    ) {
        this.exerciseRepository = exerciseRepository;
        this.exerciseMapper = exerciseMapper;
    }

    public ExerciseDTO createExercise(ExerciseDTO exerciseDTO) {
        Exercise exercise = exerciseMapper.toEntity(exerciseDTO);
        Exercise saverExercise = exerciseRepository.save(exercise);
        return exerciseMapper.toDto(saverExercise);
    }

    public ExerciseDTO getExerciseById(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado com id: " +id));

        return exerciseMapper.toDto(exercise);
    }

    public List<ExerciseDTO> getAllExercises() {
        return exerciseRepository.findAll().stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseDTO> getExerciseByMuscleGroup(MuscleGroup muscleGroup) {
        return exerciseRepository.findByMuscleGroup(muscleGroup).stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ExerciseDTO> searchExercisesByName(String name) {
        return exerciseRepository.findByNameContainingIgnoreCase(name).stream()
                .map(exerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public ExerciseDTO updateExercise(Long id, ExerciseDTO exerciseDTO) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não econtrado com id: " + id));

        exercise.setName(exerciseDTO.name());
        exercise.setDescription(exerciseDTO.description());
        exercise.setMuscleGroup(exerciseDTO.muscleGroup());
        exercise.setEquipment(exerciseDTO.equipment());
        exercise.setDifficultyLevel(exerciseDTO.difficultyLevel());
        exercise.setImageUrl(exerciseDTO.imageUrl());

        Exercise updatedExercise = exerciseRepository.save(exercise);
        return exerciseMapper.toDto(updatedExercise);
    }

    public void deleteExercise(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Exercício não encontrado com id: " + id);
        }

        exerciseRepository.deleteById(id);
    }
}
