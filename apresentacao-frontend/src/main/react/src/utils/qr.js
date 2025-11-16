import QRCode from 'qrcode';

export const gerarQrCodeDataUrl = async (texto, opcoes = {}) => {
  try {
    return await QRCode.toDataURL(texto, {
      width: 256,
      margin: 2,
      color: {
        dark: '#21193A',
        light: '#FFFFFF',
      },
      ...opcoes,
    });
  } catch (error) {
    console.error('Erro ao gerar QR Code:', error);
    return '';
  }
};
