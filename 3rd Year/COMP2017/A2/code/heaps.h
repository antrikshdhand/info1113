#ifndef HEAPS_H
#define HEAPS_H

#define NUM_REGS 32
#define BYTES_PER_BANK 64

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

typedef struct
{
    uint8_t index;
    uint8_t bank_size; // bytes allocated in current bank, is 0 if unallocated
    uint8_t malloc_size; // bytes allocated in malloc()
    uint8_t array[BYTES_PER_BANK];
    uint8_t position_if_multiple;
} bank_t;

typedef struct node
{
    bank_t bank;
    struct node* next;
} nodes_t;

/* THE "CPU" */
typedef struct cpu
{
    uint8_t instr_mem[1024];
    uint8_t data_mem[1024];
    uint32_t reg[NUM_REGS];
    uint32_t pc;
    nodes_t* heap;
    int running;
} cpu_t;

/* HEAPS */
void intialise_heap_banks(cpu_t* state);
uint32_t my_malloc(cpu_t* state, uint32_t bytes);
uint32_t my_free(cpu_t* state, uint32_t addr);

/* LINKED LIST */
nodes_t* list_init();
void list_add(nodes_t** head, bank_t bank);
void list_print(nodes_t* head);
nodes_t* list_next(nodes_t* node);
void list_delete(nodes_t** head, nodes_t* node);
void list_free(struct node** head);

#endif