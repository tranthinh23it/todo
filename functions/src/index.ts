import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp();

export const onNotificationCreated = functions.firestore
  .document('notifications/{notificationId}')
  .onCreate(async (snap, context) => {
    const data = snap.data();
    console.log('Notification created:', data);
    // Bạn có thể thêm logic xử lý ở đây (gửi push, ghi log, v.v.)
    return null;
  });
