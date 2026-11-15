<script>
export default {
  onLaunch() {},
  onShow() {},
  onHide() {}
}
</script>

<style lang="scss">
page {
  background:
    radial-gradient(circle at top right, rgba(255, 219, 188, 0.42), transparent 35%),
    linear-gradient(180deg, #f8f1e8 0%, #f4f6fb 100%);
  color: #2f261f;
  font-family: "Microsoft YaHei", sans-serif;
}

.shell {
  padding: 28rpx;
}

.panel {
  background: rgba(255, 255, 255, 0.82);
  border-radius: 28rpx;
  box-shadow: 0 18rpx 48rpx rgba(85, 62, 43, 0.12);
  padding: 24rpx;
  margin-bottom: 22rpx;
}

.hero-card {
  padding: 34rpx 28rpx;
  border-radius: 32rpx;
  background:
    radial-gradient(circle at top right, rgba(255, 240, 218, 0.75), transparent 30%),
    linear-gradient(135deg, #60422f, #243547);
  color: #fff;
}

.hero-title {
  font-size: 44rpx;
  font-weight: 700;
  line-height: 1.35;
}

.hero-sub {
  margin-top: 14rpx;
  color: rgba(255, 255, 255, 0.8);
  font-size: 26rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 700;
  margin-bottom: 18rpx;
}

.chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.chip {
  padding: 12rpx 20rpx;
  border-radius: 999rpx;
  background: #f2e1cf;
  color: #82552d;
  font-size: 24rpx;
}

.primary-btn {
  margin-top: 20rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #d08c48, #b86a2e);
  color: #fff;
}
</style>
