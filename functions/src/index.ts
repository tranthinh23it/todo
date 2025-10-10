// functions/src/index.ts
import { onDocumentCreated } from "firebase-functions/v2/firestore";
import { onCall } from "firebase-functions/v2/https";
import * as admin from "firebase-admin";

admin.initializeApp();

/**
 * Trigger khi tạo mới TaskActivity -> gửi FCM cho các user liên quan
 */
export const onTaskActivityCreated = onDocumentCreated(
  "task_activities/{activityId}",
  async (event) => {
    const activity = event.data?.data();
    if (!activity) {
      console.log("No data found");
      return;
    }

    console.log("TaskActivity created:", activity);

    try {
      await sendFCMNotifications(activity);
      console.log("Notifications sent successfully");
    } catch (error) {
      console.error("Error sending notifications:", error);
    }
  }
);

/**
 * Gửi FCM theo danh sách notifiedUserIds trong activity
 */
async function sendFCMNotifications(activity: any) {
  const notified: string[] = activity.notifiedUserIds || [];
  if (!notified.length) {
    console.log("No users to notify");
    return;
  }

  const db = admin.firestore();
  // Lấy tokens 1 lần (hỗ trợ fcmToken và fcmTokens[])
  const usersSnap = await db
    .collection("users")
    .where(admin.firestore.FieldPath.documentId(), "in", notified)
    .get();

  const tokenSet = new Set<string>();
  usersSnap.forEach((doc) => {
    const d = doc.data();
    if (typeof d.fcmToken === "string" && d.fcmToken) tokenSet.add(d.fcmToken);
    if (Array.isArray(d.fcmTokens)) {
      d.fcmTokens.forEach((t: string) => t && tokenSet.add(t));
    }
  });

  const tokens = Array.from(tokenSet);
  if (!tokens.length) {
    console.log("No FCM tokens found");
    return;
  }

  const type  = determineNotificationType(activity);
  const title = String(activity.action || "Task Update");
  const body  = String(activity.note   || "You have a new task update");

  // Tất cả giá trị trong data phải là string
  const data: { [k: string]: string } = {
    activityId: String(activity.id || ""),
    taskId:     String(activity.taskId || ""),
    projectId:  String(activity.projectId || ""),
    type:       type,
    // KHỚP VỚI CLIENT: dùng deep_link (snake_case)
    deep_link:  `todoapp://task/${String(activity.taskId || "")}`
    // Nếu bạn muốn deep link theo project thay vì task:
    // deep_link: `todoapp://project/${String(activity.projectId || "")}?tab=activity`
  };

  const res = await admin.messaging().sendEachForMulticast({
    tokens,
    // Cho Android tự hiển thị khi app background
    notification: { title, body },
    data,
    android: {
      priority: "high",
      notification: {
        channelId: "task_notifications",
        sound: "default",
      },
    },
  });

  // Dọn token lỗi (khuyến nghị xoá khỏi DB nếu lưu dạng mảng)
  const invalid: string[] = [];
  res.responses.forEach((r, i) => {
    if (!r.success && r.error?.code?.includes("registration-token")) {
      invalid.push(tokens[i]);
    }
  });
  if (invalid.length) {
    console.log("Invalid tokens:", invalid.length);
    // TODO: remove invalid tokens khỏi user docs nếu bạn lưu fcmTokens[]
  }
}

/**
 * Chuẩn hoá loại thông báo theo action
 */
function determineNotificationType(activity: any): string {
  const act = String(activity.action || "").toLowerCase();
  if (act.includes("assigned")) return "task_assigned";
  if (act.includes("comment"))  return "task_commented";
  if (act.includes("status"))   return "status_changed";
  if (act.includes("project"))  return "project_updated";
  return "task_activity";
}

/**
 * Callable function để test thủ công
 */
export const sendTestNotification = onCall(async (request) => {
  try {
    const { userId, title, body, type, taskId, projectId } = request.data || {};
    if (!userId) throw new Error("userId is required");

    const user = await admin.firestore().collection("users").doc(String(userId)).get();
    const d = user.data();

    const token = d?.fcmToken;
    if (!token) throw new Error("User FCM token not found");

    const msgTitle = String(title || "Test Notification");
    const msgBody  = String(body  || "This is a test notification");

    const data: { [k: string]: string } = {
      type:     String(type   || "test"),
      taskId:   String(taskId || ""),
      projectId:String(projectId || ""),
      // KHỚP VỚI CLIENT:
      deep_link:`todoapp://task/${String(taskId || "")}`
      // hoặc:
      // deep_link:`todoapp://project/${String(projectId || "")}?tab=activity`
    };

    await admin.messaging().send({
      token,
      notification: { title: msgTitle, body: msgBody },
      data,
      android: {
        priority: "high",
        notification: { channelId: "task_notifications", sound: "default" },
      },
    });

    return { success: true, message: "Test notification sent" };
  } catch (error) {
    console.error("Error sending test notification:", error);
    throw new Error("Failed to send notification");
  }
});
