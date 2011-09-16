function [systolic, diastolic, heart_rate] = signal_processing_program(pressure_array, time_array)

%Random functions that will have to be replaced later;
systolic = mean(pressure_array);

diastolic = sum(pressure_array);

heart_rate = mean(time_array);