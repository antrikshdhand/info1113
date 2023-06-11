#include "heaps.h"

// #define DEBUG 1

void intialise_heap_banks(cpu_t* state)
{
    nodes_t* list_head = list_init();

    for (int i = 0; i < 128; i++)
    {
        bank_t bank_i = {
            .index = i,
            .bank_size = 0x0,
            .malloc_size = 0x0,
            .array = {0},
            .position_if_multiple = 0x0
        };

        list_add(&list_head, bank_i);
    }
    state->heap = list_head;
}

uint32_t my_malloc(cpu_t* state, uint32_t bytes)
{
    // 1. Calculate n = number of banks required
    uint8_t num_banks = ceil(bytes/BYTES_PER_BANK) + 1;

    #ifdef DEBUG
    printf("bytes requested: 0x%08x\n", bytes);
    printf("num banks needed: 0x%08x\n", num_banks);
    #endif

    // 2. Iterate through the physical memory linked list and check whether
    // there are n banks free in a row
    
    nodes_t* ptr = state->heap->next;
    nodes_t* start_consec_p = state->heap->next;
    
    int consecutive_counter;
    if (state->heap->next->bank.bank_size == 0)
    {
        consecutive_counter = 1;
    }
    else
    {
        consecutive_counter = 0;
    }

    while (ptr != NULL)
    {
        #ifdef DEBUG
        printf("consec_counter: %d\n", consecutive_counter);
        printf("ptr: %d\n", ptr->bank.index);
        printf("start_consec_p: %d\n", start_consec_p->bank.index);
        #endif

        // If there are enough free heap banks for the malloc request
        if (consecutive_counter == num_banks)
        {
            #ifdef DEBUG
            printf("enough free banks!\n");
            #endif

            // The node at the start of the consecutive series of banks is
            // pointed to by start_consec_p
            if (num_banks == 1)
            {
                #ifdef DEBUG
                printf("1 bank only\n");
                #endif

                start_consec_p->bank.bank_size = bytes;
                start_consec_p->bank.malloc_size = bytes;
            }
            else
            {
                #ifdef DEBUG
                printf("more than 1 bank\n");
                #endif

                // Fill each heap bank with relevant metadata
                nodes_t* ptr_copy = start_consec_p;


                for (int i = 0; i < num_banks; i++)
                {
                    #ifdef DEBUG
                    printf("ptr_copy is at bank: %08x\n", ptr_copy->bank.index);
                    #endif

                    // All heap banks except the last are full
                    if (i != num_banks - 1)
                    {
                        #ifdef DEBUG
                        printf("Bank size before: %x\n", ptr_copy->bank.bank_size);
                        #endif

                        ptr_copy->bank.bank_size = BYTES_PER_BANK;
                        
                        #ifdef DEBUG
                        printf("Bank size after: %x\n", ptr_copy->bank.bank_size);
                        #endif
                    }
                    else
                    {
                        #ifdef DEBUG
                        printf("Bank size before: %x\n", ptr_copy->bank.bank_size);
                        #endif

                        ptr_copy->bank.bank_size = bytes % BYTES_PER_BANK;

                        #ifdef DEBUG
                        printf("Bank size after: %x\n", ptr_copy->bank.bank_size);
                        #endif
                    }

                    ptr_copy->bank.malloc_size = bytes;
                    ptr_copy->bank.position_if_multiple = i + 1;

                    ptr_copy = ptr_copy->next;
                }
            }

            #ifdef DEBUG
            printf("start_consec_p bank size: %d\n", start_consec_p->bank.bank_size);
            printf("start_consec_p bank index: %d\n", start_consec_p->bank.index);
            #endif

            uint32_t virt_mem_addr = 0xb700 + 
                                (BYTES_PER_BANK * start_consec_p->bank.index);
            
            #ifdef DEBUG
            printf("virt_add: %08x\n", virt_mem_addr);
            #endif

            return virt_mem_addr;
        }

        // Otherwise, continue checking for free heap banks
        if (ptr->bank.bank_size == 0)
        {
            consecutive_counter++;
        }
        else
        {
            consecutive_counter = 0;
            start_consec_p = list_next(start_consec_p);
        }
        
        ptr = list_next(ptr);
    }

    return -1; // Could not find any space to allocate memory.
}

uint32_t my_free(cpu_t* state, uint32_t addr)
{
    #ifdef DEBUG
    printf("FREE called on address %08x = %d\n", addr, addr);
    #endif

    if (addr < 0xb700 || addr > 0xd700)
    {
        return -1;
    }

    return 0;
}

/* LINKED LIST IMPLEMENTATION */
/* TAKEN FROM WEEK 4 TUTORIAL Q2 */

nodes_t* list_init() 
{
    // Create a new head node and return it
    nodes_t* head = calloc(1, sizeof(nodes_t));
    head->bank.index = -1;
    return head;
}

void list_add(nodes_t** head, bank_t bank) 
{
    // Add a new value to the end of the list

    // Iterate until we find the end of the list
    nodes_t* node = *head;
    while (node->next != NULL) {
        node = node->next;
    }

    // Create the new node.
    node->next = calloc(1, sizeof(nodes_t));
    node->next->bank = bank;

    return;
}

void list_print(nodes_t* head)
{
    int i = 0;
    nodes_t* node = head->next;
    while (node != NULL) {
        printf("Node #%d: %hhu\n", i+1, node->bank.index);
        node = list_next(node);
        i++;
    }
}

nodes_t* list_next(nodes_t* node) 
{
    return node->next;
}

void list_delete(nodes_t** head, nodes_t* node) 
{
    // Delete the specified node from the list.

    // We have to be careful when the node to delete is actually the head node.
    if (node == *head) {
        *head = node->next;
        free(node);
        return;
    }

    // Otherwise, we need to cut this node out, and stitch the other nodes around it back together.
    // We start by searching for the node before the one we are deleting.
    nodes_t* before = *head;
    while (before->next != node) {
        before = before->next;
    }

    // We then stitch the node before and the node after together.
    before->next = node->next;

    // Then we delete the node.
    free(node);

    return;
}

void list_free(struct node** head) 
{
    // Delete the entire list.

    // We iterate through the list, deleting the last node we visited as we go.
    struct node* node = *head;
    while (node != NULL) {
        struct node* last_node = node;
        node = node->next;

        // Delete the node we just visited.
        free(last_node);
    }

    // Remove the reference to the head node from the function caller.
    *head = NULL;

    return;
}
