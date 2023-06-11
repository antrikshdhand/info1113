#ifndef INSTRUCTIONS_H
#define INSTRUCTIONS_H

#include <stdint.h>
#include "enums.h"
#include "heaps.h"
#include "helpers.h"

void mem_trap_store(cpu_t* state, uint32_t addr, uint32_t data);
uint32_t mem_trap_load(cpu_t* state, uint32_t addr);
void increment_pc(cpu_t* state);

void execute_instr(uint32_t instr, cpu_t* state);
void JAL_execute_instr(uint32_t instr, cpu_t* state);
void LUI_execute_instr(uint32_t instr, cpu_t* state);
void JALR_execute_instr(uint32_t instr, cpu_t* state);
void B_execute_instr(uint32_t instr, cpu_t* state);
void LOAD_execute_instr(uint32_t instr, cpu_t* state);
void S_execute_instr(uint32_t instr, cpu_t* state);
void IMM_execute_instr(uint32_t instr, cpu_t* state);
void R_execute_instr(uint32_t instr, cpu_t* state);

#endif