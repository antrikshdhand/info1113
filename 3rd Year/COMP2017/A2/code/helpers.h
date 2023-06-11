#ifndef HELPERS_H
#define HELPERS_H

#include <stdint.h>
#include "heaps.h"

/* HELPER FUNCTIONS FOR ALL INSTRUCTION TYPES */
int MSB_set(uint32_t instr);
uint32_t get_opcode(uint32_t instr);
uint32_t get_rd(uint32_t instr);
uint32_t get_funct3(uint32_t instr);
uint32_t get_rs1(uint32_t instr);
uint32_t get_rs2(uint32_t instr);
uint32_t get_funct7(uint32_t instr);

uint32_t merge_next_instruction(cpu_t* state);
void register_dump(cpu_t* state);
void register_dump_wide(cpu_t* state);

/* GET IMMEDIATE VALUES FOR EACH INSTRUCTION TYPE */
int32_t U_get_imm(uint32_t instr);
int32_t J_get_imm(uint32_t instr);
int32_t I_get_imm(uint32_t instr);
int32_t B_get_imm(uint32_t instr);
int32_t S_get_imm(uint32_t instr);

#endif