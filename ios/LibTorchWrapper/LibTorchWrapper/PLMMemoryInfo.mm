#import "PLMMemoryInfo.h"

#import <mach/mach.h>
#import <mach/task.h>

@implementation PLMMemoryInfo

+ (uint64_t)physFootprintBytes {
    task_vm_info_data_t info;
    mach_msg_type_number_t count = TASK_VM_INFO_COUNT;
    const kern_return_t result =
        task_info(mach_task_self(), TASK_VM_INFO, (task_info_t)&info, &count);
    if (result != KERN_SUCCESS) {
        return 0;
    }
    // phys_footprint was added after task_vm_info_data_t itself, so a short
    // reply means the field is not present.
    if (count < TASK_VM_INFO_REV1_COUNT) {
        return 0;
    }
    return (uint64_t)info.phys_footprint;
}

+ (uint64_t)residentSizeBytes {
    mach_task_basic_info_data_t info;
    mach_msg_type_number_t count = MACH_TASK_BASIC_INFO_COUNT;
    const kern_return_t result = task_info(
        mach_task_self(), MACH_TASK_BASIC_INFO, (task_info_t)&info, &count);
    if (result != KERN_SUCCESS) {
        return 0;
    }
    return (uint64_t)info.resident_size;
}

@end
