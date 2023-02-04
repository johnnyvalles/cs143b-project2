#include <stdio.h>
#include <inttypes.h>

int main() {
    uint32_t va = 2098193;
    uint32_t pa = 0;
    
    uint32_t s = va >> 18;
    uint32_t p = ((va >> 9) & 0x1FF);
    uint32_t w = va & 0x1FF;
    uint32_t pw = va & 0x3FFFF;
    
    printf("%" PRIu32 "\n", s);
    printf("%" PRIu32 "\n", p);
    printf("%" PRIu32 "\n", w);
    printf("%" PRIu32 "\n", pw);


    printf("Virtual Address: %" PRIu32 "\n", va);


    printf("Physical Address: %" PRIu32 "\n", pa);


    return 0;
}
