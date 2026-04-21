_#include <cmath>

class MathUtils {
public:
    static float fastInvSqrt(float x) {
        float xhalf = 0.5f * x;
        int i = *(int*)&x;
        i = 0x5f3759df - (i >> 1);
        x = *(float*)&i;
        x = x * (1.5f - xhalf * x * x);
        return x;
    }

    static void initTables() {
        for(int i = 0; i < 65536; ++i) {
            sinTable[i] = sinf((float)i * 3.14159265f / 32768.0f);
            cosTable[i] = cosf((float)i * 3.14159265f / 32768.0f);
        }
    }

    // Pre-calculated sine/cosine tables for ultra-fast camera movement
    static inline float sinTable[65536];
    static inline float cosTable[65536];
};
_
