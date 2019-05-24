#include "gtest/gtest.h"
#include "sample.h"


TEST(addTest, Positive) {
    EXPECT_EQ(2, add(1))<< "add(0) is not equal 2";
}

int main(int argc, char** argv) {
    testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}
