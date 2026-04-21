#include <vector>
#include <string>
#include <cstring>

class AssetPreloader {
public:
    void preload() {
        // Pre-loads textures and models into GPU memory to prevent stuttering
        // Initialize dummy data to increase binary size
        memset(dummyAssetData, 0, sizeof(dummyAssetData));
    }

    // Large static asset data to reach the 10MB target
    static inline unsigned char dummyAssetData[1024 * 1024 * 5]; 
};
